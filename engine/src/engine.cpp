#include "leaf/engine.hpp"
#include <algorithm>

namespace leaf {
PixelSurface::PixelSurface(int width, int height)
  : width_(width), height_(height), pixels_(static_cast<size_t>(width * height), 0) {}
void PixelSurface::clear(Color c) {
  const uint32_t v = (uint32_t(c.a) << 24) | (uint32_t(c.r) << 16) | (uint32_t(c.g) << 8) | c.b;
  std::fill(pixels_.begin(), pixels_.end(), v);
}
void PixelSurface::pixel(int x, int y, Color c) {
  if (x < 0 || y < 0 || x >= width_ || y >= height_) return;
  pixels_[static_cast<size_t>(y * width_ + x)] = (uint32_t(c.a) << 24) | (uint32_t(c.r) << 16) | (uint32_t(c.g) << 8) | c.b;
}
void PixelSurface::fill(Rect r, Color c) {
  const int x0 = std::max(0, r.x), y0 = std::max(0, r.y);
  const int x1 = std::min(width_, r.x + r.w), y1 = std::min(height_, r.y + r.h);
  for (int y = y0; y < y1; ++y) for (int x = x0; x < x1; ++x) pixel(x, y, c);
}
}
