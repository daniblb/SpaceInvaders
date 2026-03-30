# Space Invaders

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square)
![Swing](https://img.shields.io/badge/UI-Swing-blue?style=flat-square)
![OOP](https://img.shields.io/badge/Paradigm-OOP-purple?style=flat-square)
![Platform](https://img.shields.io/badge/Platform-Desktop-lightgrey?style=flat-square)
![Type](https://img.shields.io/badge/Type-Ausbildungsprojekt-green?style=flat-square)

A desktop Space Invaders clone built with Java Swing as part of my FIAE apprenticeship. Features a custom game loop, OOP-based entity system, dynamic difficulty scaling, and a live HUD with animated hearts and an ammo bar.

## Gameplay

| Key | Action |
|---|---|
| `←` / `→` | Move spaceship left / right |
| `↑` | Shoot (200 ms cooldown) |

## Features

- Dynamic difficulty — alien speed and spawn rate scale with score (every 1000 points = new level)
- Alien AI — 10% of aliens can shoot back; shoot probability increases with score up to 90%
- Ammo system — start with 50 rounds, gain +2 per alien killed
- 5 lives with hit flash effect and animated heartbeat HUD
- Live scoreboard: score, time, level, ammo bar, alien stats
- Smooth acceleration/deceleration physics via `SpaceObj.approach()`

## Class Structure

| Class | Responsibility |
|---|---|
| `SpaceObj` | Abstract base — position, velocity, physics, rendering |
| `SpaceShip` | Player entity — movement bounds, ammo, lives |
| `Alien` | Enemy entity — horizontal movement, direction reversal, shooting logic |
| `Bullet` | Player projectile — upward movement at speed 15 |
| `AlienBullet` | Enemy projectile — downward movement at speed 6 |
| `SpaceInvadersMain` | Game loop, rendering, input handling, collision detection, HUD |

## Requirements

- Java 17 or higher
- No external libraries — pure Java SE (Swing, AWT, javax.sound)

## Run

```bash
javac *.java
java SpaceInvadersMain
```

The game window opens at 1900×1000 px — a fullscreen or large monitor is recommended.

## Project Structure

```
SpaceInvaders/
├── SpaceInvadersMain.java
├── SpaceObj.java
├── SpaceShip.java
├── Alien.java
├── Bullet.java
├── AlienBullet.java
└── pictures/
    ├── background.jpg
    ├── player.png
    ├── alien.png
    ├── bullet.png
    ├── bulletAlien.png
    └── heart.png
```

## What I Learned

- OOP in Java — inheritance, abstract classes, polymorphism
- Java Swing — custom painting with `paintComponent()`, event handling
- Game loop architecture — `Thread` + `Timer` for decoupled logic and spawning
- Collision detection with `Rectangle.intersects()`
- 2D rendering with `Graphics2D` — alpha compositing, anti-aliasing, custom fonts
