#include "leaf/engine.hpp"
#include <algorithm>

namespace leaf {
Engine::Engine() {
  save_.creatures.push_back(Creature{"sproutling", "Sproutling", "Nature", 1, 30, 30, 6, 0});
}
void Engine::update(double dt) { animation_time_ += std::max(0.0, std::min(dt, 0.1)); }
void Engine::move_player(int dx, int dy) {
  player_.x = std::max(8, std::min(kWidth - 8, player_.x + dx));
  player_.y = std::max(24, std::min(kHeight - 8, player_.y + dy));
}
void Engine::collect_egg() {
  ++eggs_collected_; save_.coins += 100; save_.xp += 35;
  if (save_.xp >= save_.player_level * 100) { save_.xp -= save_.player_level * 100; ++save_.player_level; save_.coins += 50; }
}
void Engine::win_battle() {
  save_.coins += 150; save_.xp += 120;
  if (!save_.creatures.empty()) { auto& c = save_.creatures.front(); c.xp += 60; if (c.xp >= c.level * 100) { c.xp -= c.level * 100; ++c.level; c.max_hp += 5; c.hp = c.max_hp; c.attack += 2; } }
}
void Engine::render(PixelSurface& s) const {
  s.clear(Color{47, 91, 57, 255});
  for (int y = 0; y < kHeight; y += 12) for (int x = 0; x < kWidth; x += 12) {
    const bool alternate = ((x / 12 + y / 12) & 1) != 0;
    s.fill(Rect{x, y, 12, 12}, alternate ? Color{62, 112, 65, 255} : Color{69, 122, 70, 255});
  }
  // Pixel-art tree clusters and path.
  s.fill(Rect{0, 205, kWidth, 18}, Color{139, 105, 67, 255});
  for (int i = 0; i < 9; ++i) {
    const int x = (i * 53 + 17) % kWidth, y = (i * 31 + 18) % 175;
    s.fill(Rect{x, y, 5, 12}, Color{91, 59, 38, 255});
    s.fill(Rect{x - 7, y - 9, 19, 12}, Color{31, 78, 48, 255});
    s.fill(Rect{x - 4, y - 13, 13, 8}, Color{53, 119, 68, 255});
  }
  // Player sprite, intentionally built from crisp pixel rectangles.
  s.fill(Rect{player_.x - 4, player_.y - 2, 8, 8}, Color{57, 124, 181, 255});
  s.fill(Rect{player_.x - 3, player_.y - 7, 6, 5}, Color{230, 190, 145, 255});
  s.fill(Rect{player_.x - 4, player_.y - 9, 8, 3}, Color{111, 69, 47, 255});
}
}
