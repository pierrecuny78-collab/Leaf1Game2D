cmake_minimum_required(VERSION 3.22)
project(LeafEngine2D LANGUAGES CXX)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
add_library(leafengine STATIC
  src/engine.cpp
  src/game.cpp
)
target_include_directories(leafengine PUBLIC include)
target_compile_options(leafengine PRIVATE -Wall -Wextra -Wpedantic)
