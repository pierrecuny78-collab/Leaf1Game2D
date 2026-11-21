extends Node2D

# Leaf1Game2D 0.1.1 — offline-first RPG prototype
const WORLD_SIZE := Vector2(2200, 1400)
const PLAYER_SPEED := 230.0
const SAVE_PATH := "user://leaf1game2d_save.json"
const EGG_POINTS := [Vector2(290, 280), Vector2(760, 370), Vector2(930, 720), Vector2(1510, 520), Vector2(1850, 1080), Vector2(1260, 1190)]
const CREATURE_POOL := ["Mousselin", "Flammèche", "Aquapup", "Sylphin", "Rocoeuf", "Luminette", "Brindibou"]

var player_pos := Vector2(470, 330)
var player_dir := Vector2.DOWN
var hp := 100
var xp := 0
var level := 1
var coins := 0
var eggs := 0
var creatures: Array = []
var collected_eggs: Array = []
var defeated_slimes: Array = []
var quest_claimed := false
var touch_vector := Vector2.ZERO
var message := "Bienvenue ! Le village a besoin de ton aide. Trouve des œufs et apprivoise leurs créatures."
var message_timer := 8.0
var enemy_attack_cooldown := 0.0
var stats_label: Label
var info_label: Label
var collection_label: Label
var collection_panel: PanelContainer
var enemies := [
	{"id":"slime_1", "pos":Vector2(650, 580), "hp":30, "alive":true},
	{"id":"slime_2", "pos":Vector2(1080, 850), "hp":30, "alive":true},
	{"id":"slime_3", "pos":Vector2(1720, 760), "hp":45, "alive":true}
]

func _ready() -> void:
	randomize()
	_load_game()
	_build_ui()
	_refresh_ui()

func _process(delta: float) -> void:
	var movement := Vector2.ZERO
	if Input.is_key_pressed(KEY_A) or Input.is_key_pressed(KEY_LEFT): movement.x -= 1.0
	if Input.is_key_pressed(KEY_D) or Input.is_key_pressed(KEY_RIGHT): movement.x += 1.0
	if Input.is_key_pressed(KEY_W) or Input.is_key_pressed(KEY_UP): movement.y -= 1.0
	if Input.is_key_pressed(KEY_S) or Input.is_key_pressed(KEY_DOWN): movement.y += 1.0
	movement += touch_vector
	if movement.length() > 1.0: movement = movement.normalized()
	if movement.length() > 0.05:
		player_dir = movement.normalized()
		player_pos += movement * PLAYER_SPEED * delta
		player_pos.x = clampf(player_pos.x, 32.0, WORLD_SIZE.x - 32.0)
		player_pos.y = clampf(player_pos.y, 100.0, WORLD_SIZE.y - 32.0)
	if message_timer > 0.0:
		message_timer -= delta
	else:
		info_label.text = "Explore la prairie • Approche-toi des objets • Interagir pour agir"
	if enemy_attack_cooldown > 0.0:
		enemy_attack_cooldown -= delta
	for enemy in enemies:
		if enemy.alive and player_pos.distance_to(enemy.pos) < 72.0 and enemy_attack_cooldown <= 0.0:
			hp = maxi(0, hp - 5)
			enemy_attack_cooldown = 1.4
			_set_message("Un Slime t'attaque ! Éloigne-toi ou utilise ATTAQUER.")
			if hp <= 0:
				hp = 50
				player_pos = Vector2(470, 330)
				_set_message("Tu as été soigné au village. Tes pièces et créatures sont conservées.")
	_refresh_ui()
	queue_redraw()

func _draw() -> void:
	var view := get_viewport_rect().size
	var camera := player_pos - view / 2.0
	camera.x = clampf(camera.x, 0.0, WORLD_SIZE.x - view.x)
	camera.y = clampf(camera.y, 0.0, WORLD_SIZE.y - view.y)
	draw_rect(Rect2(Vector2.ZERO, view), Color("#244d3b"), true)
	# Grass texture
	for x in range(0, int(WORLD_SIZE.x), 75):
		for y in range(0, int(WORLD_SIZE.y), 75):
			var p := Vector2(x, y) - camera
			if p.x > -10 and p.y > -10 and p.x < view.x + 10 and p.y < view.y + 10:
				draw_circle(p, 1.8, Color(0.58, 0.75, 0.43, 0.28))
	# Main road, village square and pond
	draw_rect(Rect2(Vector2(0, 430) - camera, Vector2(WORLD_SIZE.x, 100)), Color("#b89a68"), true)
	draw_rect(Rect2(Vector2(350, 230) - camera, Vector2(360, 290)), Color("#9b8058"), true)
	draw_rect(Rect2(Vector2(1110, 120) - camera, Vector2(300, 190)), Color("#397e9b"), true)
	draw_rect(Rect2(Vector2(1125, 135) - camera, Vector2(270, 160)), Color("#4d9db0"), true)
	# Trees around the world
	for i in range(54):
		var tree_pos := Vector2(80.0 + float((i * 173) % 2050), 100.0 + float((i * 97) % 1200))
		var p := tree_pos - camera
		if p.x > -45 and p.y > -65 and p.x < view.x + 45 and p.y < view.y + 65:
			draw_rect(Rect2(p + Vector2(-5, 5), Vector2(10, 29)), Color("#68452d"), true)
			draw_circle(p, 25, Color("#27613d"))
			draw_circle(p + Vector2(-10, -9), 15, Color("#34794a"))
	# Eggs
	for i in range(EGG_POINTS.size()):
		if collected_eggs.has(i): continue
		var ep: Vector2 = EGG_POINTS[i] - camera
		if ep.x > -25 and ep.y > -25 and ep.x < view.x + 25 and ep.y < view.y + 25:
			draw_circle(ep, 15, Color("#f2d36b"))
			draw_arc(ep, 15, 0.0, TAU, 24, Color("#fff2bd"), 3.0)
			draw_circle(ep + Vector2(-5, -4), 3, Color("#fff8d8"))
	# NPC
	var npc := Vector2(520, 390) - camera
	draw_circle(npc, 14, Color("#d6a64c"))
	draw_circle(npc + Vector2(0, -14), 9, Color("#f0d6b2"))
	draw_string(ThemeDB.fallback_font, npc + Vector2(-38, -29), "Guide", HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.WHITE)
	# Slimes
	for enemy in enemies:
		if not enemy.alive: continue
		var ep: Vector2 = enemy.pos - camera
		if ep.x > -30 and ep.y > -30 and ep.x < view.x + 30 and ep.y < view.y + 30:
			draw_circle(ep, 17, Color("#79d66c"))
			draw_circle(ep + Vector2(-6, -3), 2.5, Color("#173c27"))
			draw_circle(ep + Vector2(6, -3), 2.5, Color("#173c27"))
			draw_rect(Rect2(ep + Vector2(-15, 21), Vector2(30, 4)), Color("#502d2d"), true)
			draw_rect(Rect2(ep + Vector2(-15, 21), Vector2(30.0 * float(enemy.hp) / 45.0, 4)), Color("#e76d5b"), true)
	# Player
	var pp := player_pos - camera
	draw_circle(pp + Vector2(0, 13), 15, Color(0, 0, 0, 0.25))
	draw_circle(pp, 13, Color("#e9bd87"))
	draw_circle(pp + Vector2(0, -7), 12, Color("#4b9bd0"))
	draw_circle(pp + Vector2(0, -15), 8, Color("#e8c79b"))
	draw_circle(pp + player_dir * 8 + Vector2(0, -16), 2, Color("#18232b"))

func _build_ui() -> void:
	var ui := CanvasLayer.new()
	add_child(ui)
	var top := PanelContainer.new()
	top.position = Vector2(10, 8)
	top.size = Vector2(620, 44)
	ui.add_child(top)
	stats_label = Label.new()
	stats_label.add_theme_font_size_override("font_size", 15)
	top.add_child(stats_label)
	var bottom := PanelContainer.new()
	bottom.anchor_left = 0.04
	bottom.anchor_right = 0.96
	bottom.anchor_top = 1.0
	bottom.anchor_bottom = 1.0
	bottom.offset_top = -65
	bottom.offset_bottom = -8
	ui.add_child(bottom)
	info_label = Label.new()
	info_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	info_label.vertical_alignment = VERTICAL_ALIGNMENT_CENTER
	info_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	bottom.add_child(info_label)
	var interact := _make_button(ui, "INTERAGIR", Vector2(-150, -220), Vector2(132, 48))
	interact.pressed.connect(_interact)
	var attack := _make_button(ui, "ATTAQUER", Vector2(-150, -166), Vector2(132, 48))
	attack.pressed.connect(_attack)
	var hatch := _make_button(ui, "ÉCLORE", Vector2(-150, -112), Vector2(132, 48))
	hatch.pressed.connect(_hatch)
	var save_button := _make_button(ui, "SAUVEGARDER", Vector2(10, -58), Vector2(145, 42))
	save_button.pressed.connect(_save_game)
	var collection_button := _make_button(ui, "COLLECTION", Vector2(162, -58), Vector2(125, 42))
	collection_button.pressed.connect(_toggle_collection)
	var directions := HBoxContainer.new()
	directions.position = Vector2(12, -165)
	directions.anchor_top = 1.0
	directions.anchor_bottom = 1.0
	directions.add_theme_constant_override("separation", 3)
	ui.add_child(directions)
	for item in [["↑", Vector2.UP], ["←", Vector2.LEFT], ["↓", Vector2.DOWN], ["→", Vector2.RIGHT]]:
		var b := Button.new()
		b.text = item[0]
		b.custom_minimum_size = Vector2(43, 48)
		b.button_down.connect(func(): touch_vector = item[1])
		b.button_up.connect(func(): touch_vector = Vector2.ZERO)
		directions.add_child(b)
	collection_panel = PanelContainer.new()
	collection_panel.position = Vector2(12, 60)
	collection_panel.size = Vector2(250, 220)
	collection_panel.visible = false
	ui.add_child(collection_panel)
	collection_label = Label.new()
	collection_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	collection_panel.add_child(collection_label)

func _make_button(parent: Node, caption: String, offset: Vector2, button_size: Vector2) -> Button:
	var b := Button.new()
	b.text = caption
	b.anchor_left = 1.0
	b.anchor_right = 1.0
	b.anchor_top = 1.0
	b.anchor_bottom = 1.0
	b.offset_left = offset.x
	b.offset_right = offset.x + button_size.x
	b.offset_top = offset.y
	b.offset_bottom = offset.y + button_size.y
	b.add_theme_font_size_override("font_size", 15)
	parent.add_child(b)
	return b

func _interact() -> void:
	for i in range(EGG_POINTS.size()):
		if not collected_eggs.has(i) and player_pos.distance_to(EGG_POINTS[i]) < 82.0:
			collected_eggs.append(i)
			eggs += 1
			coins += 5
			_set_message("Œuf mystérieux récupéré ! +5 pièces. Tu peux maintenant le faire éclore.")
			_save_game()
			return
	if player_pos.distance_to(Vector2(520, 390)) < 105.0:
		if not quest_claimed:
			if collected_eggs.size() >= 2 and creatures.size() >= 1:
				quest_claimed = true
				coins += 50
				_add_xp(60)
				_set_message("Quête terminée : Les premiers compagnons ! +50 pièces et +60 XP.")
			else:
				_set_message("Guide : récupère 2 œufs et fais éclore au moins une créature. Récompense : 50 pièces + 60 XP.")
		else:
			_set_message("Guide : bravo, aventurier ! D'autres missions arrivent bientôt.")
		return
	_set_message("Rien à proximité. Cherche les œufs dorés, le guide ou un Slime.")

func _hatch() -> void:
	if eggs <= 0:
		_set_message("Tu n'as aucun œuf. Explore la prairie pour en trouver.")
		return
	eggs -= 1
	var creature: String = CREATURE_POOL[randi() % CREATURE_POOL.size()]
	creatures.append(creature)
	_add_xp(25)
	_set_message("Éclosion ! %s rejoint ta collection. +25 XP." % creature)
	_save_game()

func _attack() -> void:
	var target_index := -1
	var nearest := 999999.0
	for i in range(enemies.size()):
		var enemy = enemies[i]
		if enemy.alive:
			var distance := player_pos.distance_to(enemy.pos)
			if distance < 95.0 and distance < nearest:
				nearest = distance
				target_index = i
	if target_index == -1:
		_set_message("Aucun ennemi à portée. Approche-toi d'un Slime.")
		return
	var target = enemies[target_index]
	target.hp -= 20
	if target.hp <= 0:
		target.alive = false
		defeated_slimes.append(target.id)
		coins += 12
		_add_xp(35)
		_set_message("Slime vaincu ! +12 pièces et +35 XP.")
	else:
		_set_message("Coup porté au Slime ! Il lui reste %d PV." % target.hp)
	_save_game()

func _add_xp(amount: int) -> void:
	xp += amount
	while xp >= level * 100:
		xp -= level * 100
		level += 1
		hp = 100
		_set_message("NIVEAU %d ! Tes PV sont restaurés." % level)

func _toggle_collection() -> void:
	collection_panel.visible = not collection_panel.visible
	_refresh_ui()

func _refresh_ui() -> void:
	if stats_label == null: return
	stats_label.text = "Niv. %d  ♥ %d/100  XP %d/%d  ◈ %d  Œufs %d  Compagnons %d" % [level, hp, xp, level * 100, coins, eggs, creatures.size()]
	if collection_label != null:
		var summary := "COLLECTION\n"
		if creatures.is_empty():
			summary += "Aucune créature pour le moment.\nFais éclore ton premier œuf !"
		else:
			for creature in creatures:
				summary += "• " + str(creature) + "\n"
		summary += "\nQuête du guide : " + ("Terminée ✓" if quest_claimed else "%d/2 œufs • %d/1 éclosion" % [mini(collected_eggs.size(), 2), mini(creatures.size(), 1)])
		collection_label.text = summary

func _set_message(value: String) -> void:
	message = value
	message_timer = 5.0
	if info_label != null: info_label.text = message

func _save_game() -> void:
	var data := {
		"player_pos":[player_pos.x, player_pos.y], "hp":hp, "xp":xp, "level":level,
		"coins":coins, "eggs":eggs, "creatures":creatures, "collected_eggs":collected_eggs,
		"defeated_slimes":defeated_slimes, "quest_claimed":quest_claimed
	}
	var file := FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	if file:
		file.store_string(JSON.stringify(data))
		_set_message("Progression sauvegardée sur cet appareil.")

func _load_game() -> void:
	if not FileAccess.file_exists(SAVE_PATH): return
	var file := FileAccess.open(SAVE_PATH, FileAccess.READ)
	if file == null: return
	var parsed = JSON.parse_string(file.get_as_text())
	if typeof(parsed) != TYPE_DICTIONARY: return
	var data: Dictionary = parsed
	if data.has("player_pos") and data.player_pos is Array and data.player_pos.size() >= 2:
		player_pos = Vector2(float(data.player_pos[0]), float(data.player_pos[1]))
	hp = int(data.get("hp", 100))
	xp = int(data.get("xp", 0))
	level = int(data.get("level", 1))
	coins = int(data.get("coins", 0))
	eggs = int(data.get("eggs", 0))
	creatures.assign(data.get("creatures", []))
	collected_eggs.assign(data.get("collected_eggs", []))
	defeated_slimes.assign(data.get("defeated_slimes", []))
	quest_claimed = bool(data.get("quest_claimed", false))
	for enemy in enemies:
		if defeated_slimes.has(enemy.id): enemy.alive = false
