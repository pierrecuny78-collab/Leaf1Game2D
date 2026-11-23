cmake_minimum_required(VERSION 3.22.1)
project(leafengine LANGUAGES CXX)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(leafengine SHARED
  src/engine.cpp
  src/scene.cpp
  src/game.cpp
  src/jni_bridge.cpp
)
target_include_directories(leafengine PRIVATE include)
target_compile_options(leafengine PRIVATE -Wall -Wextra -Wpedantic)

option(LEAF_BUILD_TESTS "Build host-side LeafEngine2D tests" OFF)
if(LEAF_BUILD_TESTS)
  enable_testing()
  add_executable(leaf_scene_tests
    tests/scene_test.cpp
    src/scene.cpp
    src/engine.cpp
  )
  target_include_directories(leaf_scene_tests PRIVATE include)
  target_compile_options(leaf_scene_tests PRIVATE -Wall -Wextra -Wpedantic)
  add_test(NAME leaf_scene_tests COMMAND leaf_scene_tests)

  add_executable(leaf_game_tests
    tests/game_test.cpp
    src/game.cpp
    src/engine.cpp
  )
  target_include_directories(leaf_game_tests PRIVATE include)
  target_compile_options(leaf_game_tests PRIVATE -Wall -Wextra -Wpedantic)
  add_test(NAME leaf_game_tests COMMAND leaf_game_tests)
endif()
