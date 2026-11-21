#include "leaf/scene.hpp"
#include <cassert>

int main() {
  leaf::PixelSurface surface(32, 32);
  surface.clear(leaf::Color{0, 0, 0, 255});

  leaf::Scene scene("TestScene");
  leaf::SceneNode parent;
  parent.name = "Parent";
  parent.position = {4, 5};

  leaf::SceneNode rectangle;
  rectangle.name = "RedRectangle";
  rectangle.kind = leaf::NodeKind::Rectangle2D;
  rectangle.position = {3, 2};
  rectangle.size = {6, 4};
  rectangle.color = {255, 0, 0, 255};
  parent.add_child(rectangle);
  scene.root().add_child(parent);

  scene.render(surface);
  const auto& pixels = surface.pixels();
  const auto red = (uint32_t(255) << 24) | (uint32_t(255) << 16);
  const auto black = (uint32_t(255) << 24);
  assert(pixels[7 * 32 + 7] == red);
  assert(pixels[4 * 32 + 4] == black);

  scene.root().children[0].visible = false;
  surface.clear(leaf::Color{0, 0, 0, 255});
  scene.render(surface);
  assert(surface.pixels()[7 * 32 + 7] == black);
  return 0;
}
