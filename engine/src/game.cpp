#include "leaf/engine.hpp"
#include <algorithm>

namespace leaf {
namespace {
void draw_sprite(PixelSurface& surface, int x, int y, const char* const* rows, int width) {
  for (int row = 0; rows[row] != nullptr; ++row) {
    for (int col = 0; col < width; ++col) {
      const char px = rows[row][col];
      Color color{};
      bool visible = true;
      switch (px) {
        case '.': visible = false; break;
        case 'K': color = Color{38, 43, 54, 255}; break;
        case 'B': color = Color{48, 91, 153, 255}; break;
        case 'b': color = Color{78, 139, 204, 255}; break;
        case 'S': color = Color{222, 171, 126, 255}; break;
        case 'H': color = Color{91, 55, 39, 255}; break;
        case 'G': color = Color{69, 157, 91, 255}; break;
        case 'g': color = Color{115, 201, 112, 255}; break;
        case 'Y': color = Color{247, 211, 93, 255}; break;
        case 'W': color = Color{255, 244, 204, 255}; break;
        case 'P': color = Color{139, 91, 190, 255}; break;
        default: visible = false; break;
      }
      if (visible) surface.pixel(x + col, y + row, color);
    }
  }
}

constexpr const char* kPlayerSprite[] = {
  "......HHHH......",
  ".....HHHHHH.....",
  ".....SSSSSS.....",
  ".....SKKSKS.....",
  "......SSSS......",
  ".....BBBBBB.....",
  "....BBBBBBBB....",
  "....BBbBBbBB....",
  "....BBBBBBBB....",
  ".....BBBBBB.....",
  ".....BB..BB.....",
  ".....BB..BB.....",
  "....HH....HH....",
  "....HH....HH....",
  "................",
  nullptr
};

constexpr const char* kSproutlingSprite[] = {
  ".......GG.......",
  "......GGGG......",
  ".....GGgGGG.....",
  "......GGGG......",
  "....GGGGGGGG....",
  "...GGGGGGGGGG...",
  "...GGKKGGKKGG...",
  "...GGGGGGGGGG...",
  "....GGGGGGGG....",
  ".....GGGGGG.....",
  "......GGGG......",
  ".....GG..GG.....",
  "....GG....GG....",
  "................",
  "................",
  "................",
  nullptr
};

constexpr const char* kEggSprite[] = {
  "......YYYY......",
  ".....YWWYYY.....",
  "....YYWWWWYY....",
  "...YYWWWWWWYY...",
  "...YYYYWWYYYY...",
  "...YYYYYYYYYY...",
  "....YYYYYYYY....",
  ".....YYYYYY.....",
  "......YYYY......",
  ".......YY.......",
  "................",
  "................",
  "................",
  "................",
  "................",
  "................",
  nullptr
};
}

Engine::Engine() {
  save_.creatures.push_back(Creature{"sproutling", "Sproutling", "Nature", 1, 30, 30, 6, 0});
}
void Engine::update(double dt) { animation_time_ += std::max(0.0, std::min(dt, 0.1)); }
void Engine::move_player(int dx, int dy) {
  player_.x = std::max(8, std::min(kWidth - 8, player_.x + dx));
  player_.y = std::max(24, std::min(kHeight - 8, player_.y + dy));
}
void Engine::restore_progress(int coins, int xp, int level, int eggs) {
  save_.coins = std::max(0, coins);
  save_.xp = std::max(0, xp);
  save_.player_level = std::max(1, level);
  save_.eggs_collected = std::max(0, eggs);
}
void Engine::collect_egg() {
  ++save_.eggs_collected;
  save_.coins += 100;
  save_.xp += 35;
  while (save_.xp >= save_.player_level * 100) {
    save_.xp -= save_.player_level * 100;
    ++save_.player_level;
    save_.coins += 50;
  }
}
void Engine::win_battle() {
  save_.coins += 150;
  save_.xp += 120;
  while (save_.xp >= save_.player_level * 100) {
    save_.xp -= save_.player_level * 100;
    ++save_.player_level;
    save_.coins += 50;
  }
  if (!save_.creatures.empty()) {
    auto& c = save_.creatures.front();
    c.xp += 60;
    while (c.xp >= c.level * 100) {
      c.xp -= c.level * 100;
      ++c.level;
      c.max_hp += 5;
      c.hp = c.max_hp;
      c.attack += 2;
    }
  }
}
void Engine::render(PixelSurface& s) const {
  s.clear(Color{47, 91, 57, 255});
  for (int y = 0; y < kHeight; y += 12) for (int x = 0; x < kWidth; x += 12) {
    const bool alternate = ((x / 12 + y / 12) & 1) != 0;
    s.fill(Rect{x, y, 12, 12}, alternate ? Color{62, 112, 65, 255} : Color{69, 122, 70, 255});
  }
  s.fill(Rect{0, 205, kWidth, 18}, Color{139, 105, 67, 255});
  for (int i = 0; i < 9; ++i) {
    const int x = (i * 53 + 17) % kWidth, y = (i * 31 + 18) % 175;
    s.fill(Rect{x, y, 5, 12}, Color{91, 59, 38, 255});
    s.fill(Rect{x - 7, y - 9, 19, 12}, Color{31, 78, 48, 255});
    s.fill(Rect{x - 4, y - 13, 13, 8}, Color{53, 119, 68, 255});
  }

  // World props and characters now use readable 16x16 pixel-art sprites.
  draw_sprite(s, 92, 148, kEggSprite, 16);
  draw_sprite(s, 330, 132, kSproutlingSprite, 16);
  draw_sprite(s, player_.x - 8, player_.y - 8, kPlayerSprite, 16);
}
}
