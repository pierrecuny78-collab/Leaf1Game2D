#pragma once
#include "leaf/engine.hpp"
#include <string>
#include <vector>

namespace leaf {
enum class NodeKind { Node2D, Sprite2D, Rectangle2D, Camera2D };
struct SceneNode {
  std::string name{"Node2D"};
  NodeKind kind{NodeKind::Node2D};
  Vec2i position{};
  Vec2i size{};
  Color color{255, 255, 255, 255};
  bool visible{true};
  std::vector<SceneNode> children;
  SceneNode& add_child(SceneNode child);
};
class Scene {
public:
  explicit Scene(std::string name = "Scene");
  const std::string& name() const { return name_; }
  SceneNode& root() { return root_; }
  const SceneNode& root() const { return root_; }
  void render(PixelSurface& target) const;
private:
  static void render_node(const SceneNode& node, PixelSurface& target, Vec2i parent_position);
  std::string name_;
  SceneNode root_;
};
} // namespace leaf
