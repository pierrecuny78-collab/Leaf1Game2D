extends Node2D

const WORLD_SIZE := Vector2(1800, 1100)
const PLAYER_SPEED := 235.0
const SAVE_PATH := "user://leaf1game2d_save.json"

var player_pos := Vector2(470, 330)
var player_dir := Vector2.DOWN
var player_hp := 100
var player_xp := 0
var player_level := 1
var coins := 0
var eggs := 0
var creatures: Array[String] = []
var message := "Bienvenue dans la Prairie lumineuse ! Trouve un œuf près des arbres."
var message_timer := 5.0
var world_time := 0.0
var facing_name := "Sud"
var info_label: Label
var stats_label: Label
var touch_vector := Vector2.ZERO

func _ready() -> void:
	_load_game()
	_build_ui()
	message_timer = 6.0

func _process(delta: float) -> void:
	world_time += delta
	var input_vector := Vector2.ZERO
	if Input.is_key_pressed(KEY_A) or Input.is_key_pressed(KEY_LEFT): input_vector.x -= 1
	if Input.is_key_pressed(KEY_D) or Input.is_key_pressed(KEY_RIGHT): input_vector.x += 1
	if Input.is_key_pressed(KEY_W) or Input.is_key_pressed(KEY_UP): input_vector.y -= 1
	if Input.is_key_pressed(KEY_S) or Input.is_key_pressed(KEY_DOWN): input_vector.y += 1
	input_vector += touch_vector
	if input_vector.length() > 1.0: input_vector = input_vector.normalized()
	if input_vector.length() > 0.05:
		player_dir = input_vector.normalized()
		player_pos += input_vector * PLAYER_SPEED * delta
		player_pos.x = clampf(player_pos.x, 35, WORLD_SIZE.x - 35)
		player_pos.y = clampf(player_pos.y, 90, WORLD_SIZE.y - 35)
	if message_timer > 0:
		message_timer -= delta
		info_label.text = message
	else:
		info_label.text = "Explore, cherche les œufs et approche-toi des créatures."
	stats_label.text = "Niv. %d   PV %d/100   XP %d   Pièces %d   Œufs %d   Créatures %d" % [player_level, player_hp, player_xp, coins, eggs, creatures.size()]
	queue_redraw()

func _draw() -> void:
	var camera := player_pos - get_viewport_rect().size / 2.0
	camera.x = clampf(camera.x, 0, WORLD_SIZE.x - get_viewport_rect().size.x)
	camera.y = clampf(camera.y, 0, WORLD_SIZE.y - get_viewport_rect().size.y)
	draw_rect(Rect2(Vector2.ZERO, get_viewport_rect().size), Color("#244d3b"), true)
	for x in range(0, int(WORLD_SIZE.x), 90):
		for y in range(0, int(WORLD_SIZE.y), 90):
			var p := Vector2(x, y) - camera
			if p.x > -20 and p.y > -20 and p.x < 1000 and p.y < 600:
				draw_circle(p, 2.0, Color(0.48, 0.66, 0.38, 0.35))
	draw_rect(Rect2(Vector2(0, 430) - camera, Vector2(WORLD_SIZE.x, 105)), Color("#b89a68"), true)
	draw_rect(Rect2(Vector2(1110, 120) - camera, Vector2(300, 190)), Color("#397e9b"), true)
	draw_rect(Rect2(Vector2(1125, 135) - camera, Vector2(270, 160)), Color("#4d9db0"), true)
	for i in range(34):
		var tx := 80.0 + float((i * 173) % 1600)
		var ty := 100.0 + float((i * 97) % 900)
		var p := Vector2(tx, ty) - camera
		if p.x > -50 and p.y > -70 and p.x < 1010 and p.y < 610:
			draw_rect(Rect2(p + Vector2(-5, 4), Vector2(10, 28)), Color("#68452d"), true)
			draw_circle(p, 24, Color("#27613d"))
			draw_circle(p + Vector2(-10, -8), 14, Color("#34794a"))
	for egg_pos in [Vector2(290, 280), Vector2(760, 370), Vector2(930, 720), Vector2(1510, 520)]:
		var ep: Vector2 = egg_pos - camera
		if ep.x > -30 and ep.y > -30 and ep.x < 990 and ep.y < 590:
			draw_circle(ep, 15, Color("#f2d36b"))
			draw_arc(ep, 15, 0, TAU, 24, Color("#fff2bd"), 3)
			draw_circle(ep + Vector2(-5, -4), 3, Color("#fff8d8"))
	var pp := player_pos - camera
	draw_circle(pp + Vector2(0, 13), 15, Color(0, 0, 0, 0.25))
	draw_circle(pp, 13, Color("#e9bd87"))
	draw_circle(pp + Vector2(0, -7), 12, Color("#4b9bd0"))
	draw_circle(pp + Vector2(0, -15), 8, Color("#e8c79b"))
	draw_circle(pp + player_dir * 8 + Vector2(0, -16), 2, Color("#18232b"))
	var npc := Vector2(520, 390) - camera
	draw_circle(npc, 13, Color("#d6a64c"))
	draw_circle(npc + Vector2(0, -13), 8, Color("#f0d6b2"))
	draw_string(ThemeDB.fallback_font, npc + Vector2(-44, -27), "Guide", HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.WHITE)

func _build_ui() -> void:
	var ui := CanvasLayer.new()
	add_child(ui)
	var top := PanelContainer.new()
	top.position = Vector2(12, 10)
	top.size = Vector2(570, 42)
	ui.add_child(top)
	stats_label = Label.new()
	stats_label.add_theme_font_size_override("font_size", 17)
	top.add_child(stats_label)
	var bottom := PanelContainer.new()
	bottom.anchor_left = 0.08
	bottom.anchor_right = 0.92
	bottom.anchor_top = 1
	bottom.anchor_bottom = 1
	bottom.offset_top = -74
	bottom.offset_bottom = -10
	ui.add_child(bottom)
	info_label = Label.new()
	info_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	info_label.vertical_alignment = VERTICAL_ALIGNMENT_CENTER
	info_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	bottom.add_child(info_label)
	var interact := Button.new()
	interact.text = "INTERAGIR"
	interact.position = Vector2(get_viewport_rect().size.x - 150, get_viewport_rect().size.y - 150)
	interact.size = Vector2(130, 58)
	interact.add_theme_font_size_override("font_size", 18)
	interact.pressed.connect(_interact)
	ui.add_child(interact)
	var hatch := Button.new()
	hatch.text = "ÉCLORE"
	hatch.position = Vector2(get_viewport_rect().size.x - 150, get_viewport_rect().size.y - 82)
	hatch.size = Vector2(130, 52)
	hatch.add_theme_font_size_override("font_size", 18)
	hatch.pressed.connect(_hatch)
	ui.add_child(hatch)
	var save_button := Button.new()
	save_button.text = "SAUVEGARDER"
	save_button.position = Vector2(12, get_viewport_rect().size.y - 62)
	save_button.size = Vector2(145, 42)
	save_button.pressed.connect(_save_game)
	ui.add_child(save_button)
	var directions := HBoxContainer.new()
	directions.position = Vector2(18, get_viewport_rect().size.y - 160)
	directions.add_theme_constant_override("separation", 4)
	ui.add_child(directions)
	for item in [["↑", Vector2.UP], ["←", Vector2.LEFT], ["↓", Vector2.DOWN], ["→", Vector2.RIGHT]]:
		var b := Button.new()
		b.text = item[0]
		b.custom_minimum_size = Vector2(48, 52)
		b.button_down.connect(func(): touch_vector = item[1])
		b.button_up.connect(func(): touch_vector = Vector2.ZERO)
		directions.add_child(b)

func _interact() -> void:
	for p in [Vector2(290, 280), Vector2(760, 370), Vector2(930, 720), Vector2(1510, 520)]:
		if player_pos.distance_to(p) < 85:
			eggs += 1
			coins += 5
			_set_message("Tu as trouvé un Œuf mystérieux ! +5 pièces. Appuie sur ÉCLORE.")
			_save_game()
			return
	if player_pos.distance_to(Vector2(520, 390)) < 100:
		coins += 10
		_set_message("Le guide t'offre 10 pièces : explore et collectionne les créatures !")
	else:
		_set_message("Rien à proximité. Approche-toi d'un œuf ou du guide.")

func _hatch() -> void:
	if eggs <= 0:
		_set_message("Tu n'as aucun œuf. Cherche les œufs dorés dans la prairie.")
		return
	eggs -= 1
	var pool := ["Mousselin", "Flammèche", "Aquapup", "Sylphin", "Rocoeuf"]
	var creature: String = pool[randi() % pool.size()]
	creatures.append(creature)
	player_xp += 25
	if player_xp >= player_level * 100:
		player_xp -= player_level * 100
		player_level += 1
		_set_message("Éclosion ! %s rejoint ton équipe. Niveau supérieur !" % creature)
	else:
		_set_message("Éclosion ! %s rejoint ton équipe. +25 XP." % creature)
	_save_game()

func _set_message(value: String) -> void:
	message = value
	message_timer = 5.0
	info_label.text = message

func _save_game() -> void:
	var data := {"player_pos":[player_pos.x, player_pos.y], "hp":player_hp, "xp":player_xp, "level":player_level, "coins":coins, "eggs":eggs, "creatures":creatures}
	var file := FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	if file:
		file.store_string(JSON.stringify(data))
		_set_message("Sauvegarde locale effectuée.")

func _load_game() -> void:
	if not FileAccess.file_exists(SAVE_PATH): return
	var file := FileAccess.open(SAVE_PATH, FileAccess.READ)
	if not file: return
	var parsed = JSON.parse_string(file.get_as_text())
	if typeof(parsed) != TYPE_DICTIONARY: return
	var d: Dictionary = parsed
	if d.has("player_pos"): player_pos = Vector2(d.player_pos[0], d.player_pos[1])
	player_hp = int(d.get("hp", 100))
	player_xp = int(d.get("xp", 0))
	player_level = int(d.get("level", 1))
	coins = int(d.get("coins", 0))
	eggs = int(d.get("eggs", 0))
	creatures.assign(d.get("creatures", []))
