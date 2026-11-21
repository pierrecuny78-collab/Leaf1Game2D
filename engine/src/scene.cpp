#include "leaf/scene.hpp"
#include <utility>

namespace leaf {
SceneNode& SceneNode::add_child(SceneNode child) {
  children.push_back(std::move(child));
  return children.back();
}
Scene::Scene(std::string name) : name_(std::move(name)) {
  root_.name = name_;
  root_.kind = NodeKind::Node2D;
}
void Scene::render_node(const SceneNode& node, PixelSurface& target, Vec2i parent_position) {
  if (!node.visible) return;
  const Vec2i world{parent_position.x + node.position.x, parent_position.y + node.position.y};
  if ((node.kind == NodeKind::Rectangle2D || node.kind == NodeKind::Sprite2D) && node.size.x > 0 && node.size.y > 0) {
    target.fill(Rect{world.x, world.y, node.size.x, node.size.y}, node.color);
  }
  for (const auto& child : node.children) render_node(child, target, world);
}
void Scene::render(PixelSurface& target) const { render_node(root_, target, Vec2i{}); }
} // namespace leaf
