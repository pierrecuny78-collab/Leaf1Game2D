#pragma once
#include <cstdint>
#include <string>
#include <vector>

namespace leaf {
struct Vec2i { int x{0}; int y{0}; };
struct Color { uint8_t r{0}, g{0}, b{0}, a{255}; };
struct Rect { int x{0}, y{0}, w{0}, h{0}; };
struct Sprite { int x{0}, y{0}, w{0}, h{0}; };
struct Creature { std::string id, name, element; int level{1}, hp{30}, max_hp{30}, attack{6}, xp{0}; };
struct SaveData {
  int schema{2};
  int coins{500};
  int xp{0};
  int player_level{1};
  int eggs_collected{0};
  int eggs_hatched{0};
  int last_hatch_rarity{0};
  std::vector<Creature> creatures;
};

class PixelSurface {
public:
  PixelSurface(int width, int height);
  void clear(Color color);
  void fill(Rect rect, Color color);
  void pixel(int x, int y, Color color);
  int width() const { return width_; }
  int height() const { return height_; }
  const std::vector<uint32_t>& pixels() const { return pixels_; }
private:
  int width_, height_;
  std::vector<uint32_t> pixels_;
};

class Engine {
public:
  static constexpr int kWidth = 480;
  static constexpr int kHeight = 270;
  static constexpr int kTickRate = 60;
  Engine();
  void update(double dt);
  void render(PixelSurface& target) const;
  void move_player(int dx, int dy);
  void collect_egg();
  int hatch_egg(); // 0: no egg, 1: common, 2: rare, 3: epic, 4: legendary
  void win_battle();
  void restore_progress(int coins, int xp, int level, int eggs);
  void restore_hatched(int count);
  const SaveData& save() const { return save_; }
  const Vec2i& player() const { return player_; }
private:
  SaveData save_;
  Vec2i player_{240, 135};
  double animation_time_{0.0};
  double hatch_flash_{0.0};
};
}
