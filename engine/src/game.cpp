#include "leaf/engine.hpp"
#include <algorithm>
#include <cmath>

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

constexpr const char* kEmbercubSprite[] = {
  "......PPPP......", ".....PPPPPP.....", "....PPYYYYPP....", "...PPYYYYYYPP...",
  "...PPKKYYKKPP...", "...PPYYYYYYPP...", "....PPPPPPPP....", ".....PPPPPP.....",
  "......PPPP......", ".....PP..PP.....", "....PP....PP....", "................", nullptr
};
constexpr const char* kAquaphinSprite[] = {
  ".......bbbb.....", "......bbbbbb....", ".....bbWWWWbb...", "....bbWWWWWWbb..",
  "....bbKKWWKKbb..", "....bbbbbbbbbb..", ".....bbbbbbbb...", "......bbbbbb....",
  ".......bbbb.....", "......bb..bb....", ".....bb....bb...", "................", nullptr
};
constexpr const char* kLunowlSprite[] = {
  "......PPPP......", ".....PPPPPP.....", "....PPWWWWPP....", "...PPWWWWWWPP...",
  "...PPKKWWKKPP...", "...PPWWWWWWPP...", "....PPPPPPPP....", ".....PPPPPP.....",
  "....PP..PP..PP..", "...PP...PP...PP.", "................", nullptr
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
void Engine::update(double dt) {
  const double step = std::max(0.0, std::min(dt, 0.1));
  animation_time_ += step;
  hatch_flash_ = std::max(0.0, hatch_flash_ - step);
}
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
int Engine::hatch_egg() {
  if (save_.eggs_collected <= 0) return 0;
  --save_.eggs_collected;
  ++save_.eggs_hatched;
  // Reproducible local roll. Every 10th hatch is guaranteed epic or better.
  const unsigned roll = static_cast<unsigned>((save_.eggs_hatched * 37 + save_.player_level * 19 + 11) % 100);
  int rarity = roll < 60 ? 1 : (roll < 85 ? 2 : (roll < 97 ? 3 : 4));
  if (save_.eggs_hatched % 10 == 0 && rarity < 3) rarity = 3;
  save_.last_hatch_rarity = rarity;
  hatch_flash_ = 2.4;

  static const char* names[] = {"", "Sproutling", "Embercub", "Aquaphin", "Lunowl"};
  static const char* elements[] = {"", "Nature", "Feu", "Eau", "Lumiere"};
  const int index = rarity == 1 ? 0 : (rarity == 2 ? 1 : (rarity == 3 ? 2 : 3));
  const int baseHp = 28 + rarity * 8;
  save_.creatures.push_back(Creature{
    std::string("hatch_") + std::to_string(save_.eggs_hatched), names[index + 1], elements[index + 1],
    1, baseHp, baseHp, 5 + rarity * 2, 0
  });
  return rarity;
}
void Engine::restore_hatched(int count) {
  const int target = std::max(0, std::min(count, 500));
  while (static_cast<int>(save_.creatures.size()) > target + 1) save_.creatures.pop_back();
  while (static_cast<int>(save_.creatures.size()) < target + 1) {
    const int n = static_cast<int>(save_.creatures.size());
    const int rarity = 1 + (n % 4);
    const char* names[] = {"Sproutling", "Embercub", "Aquaphin", "Lunowl"};
    const char* elements[] = {"Nature", "Feu", "Eau", "Lumiere"};
    save_.creatures.push_back(Creature{std::string("restored_") + std::to_string(n), names[(rarity - 1) % 4], elements[(rarity - 1) % 4], 1, 28 + rarity * 8, 28 + rarity * 8, 5 + rarity * 2, 0});
  }
  save_.eggs_hatched = target;
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

  // Animated pixel-art props: idle bob, egg wobble and a short hatch sparkle.
  const int bob = static_cast<int>(std::sin(animation_time_ * 5.0) * 2.0);
  const int wobble = static_cast<int>(std::sin(animation_time_ * 9.0) * (hatch_flash_ > 0.0 ? 3.0 : 1.0));
  draw_sprite(s, 92 + wobble, 148 + (hatch_flash_ > 0.0 ? 0 : bob), kEggSprite, 16);
  if (hatch_flash_ > 1.65) {
    s.fill(Rect{98, 145, 2, 5}, Color{255, 255, 230, 255});
    s.fill(Rect{105, 140, 2, 4}, Color{255, 232, 120, 255});
    s.fill(Rect{112, 149, 2, 5}, Color{255, 255, 230, 255});
  }
  if (save_.creatures.size() > 1) {
    const char* const* sprite = kSproutlingSprite;
    switch (save_.last_hatch_rarity) {
      case 2: sprite = kEmbercubSprite; break;
      case 3: sprite = kAquaphinSprite; break;
      case 4: sprite = kLunowlSprite; break;
      default: break;
    }
    draw_sprite(s, 330, 132 + bob, sprite, 16);
  } else {
    draw_sprite(s, 330, 132 + bob, kSproutlingSprite, 16);
  }
  draw_sprite(s, player_.x - 8, player_.y - 8 + bob, kPlayerSprite, 16);
  if (hatch_flash_ > 0.0) {
    const int cx = 100, cy = 145;
    for (int i = 0; i < 8; ++i) {
      const double a = animation_time_ * 3.0 + i * 0.785398;
      const int px = cx + static_cast<int>(std::cos(a) * (10 + (2.4 - hatch_flash_) * 22));
      const int py = cy + static_cast<int>(std::sin(a) * (8 + (2.4 - hatch_flash_) * 18));
      s.fill(Rect{px, py, 2, 2}, Color{255, 235, 130, 255});
    }
  }
}
}
