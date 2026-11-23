#include "leaf/engine.hpp"
#include <cassert>
#include <cstdint>

int main() {
  leaf::Engine game;
  assert(game.save().coins == 500);
  assert(game.save().player_level == 1);
  assert(game.save().creatures.size() == 1);

  game.move_player(10000, -10000);
  assert(game.player().x == leaf::Engine::kWidth - 8);
  assert(game.player().y == 24);

  game.restore_progress(0, 0, 1, 0);
  game.collect_egg();
  assert(game.save().eggs_collected == 1);
  assert(game.save().coins == 100);
  assert(game.save().xp == 35);

  game.win_battle();
  assert(game.save().coins >= 250);
  assert(game.save().creatures.front().xp == 60);

  leaf::PixelSurface frame(leaf::Engine::kWidth, leaf::Engine::kHeight);
  game.render(frame);
  bool has_player_blue = false;
  bool has_egg_gold = false;
  for (const uint32_t pixel : frame.pixels()) {
    const uint8_t r = static_cast<uint8_t>((pixel >> 16) & 0xff);
    const uint8_t g = static_cast<uint8_t>((pixel >> 8) & 0xff);
    const uint8_t b = static_cast<uint8_t>(pixel & 0xff);
    if (b > 120 && b > r + 20) has_player_blue = true;
    if (r > 220 && g > 170 && b < 150) has_egg_gold = true;
  }
  assert(has_player_blue);
  assert(has_egg_gold);
  return 0;
}
