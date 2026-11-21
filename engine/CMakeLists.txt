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
