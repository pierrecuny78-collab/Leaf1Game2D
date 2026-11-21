extends Node2D

# Leaf1Game2D 0.1.1 — offline-first RPG prototype
const WORLD_SIZE := Vector2(2200, 1400)
const PLAYER_SPEED := 230.0
const SAVE_PATH := "user://leaf1game2d_save.json"
const EGG_POINTS := [Vector2(290, 280), Vector2(760, 370), Vector2(930, 720), Vector2(1510, 520), Vector2(1850, 1080), Vector2(1260, 1190)]
const CREATURE_POOL := ["Mousselin", "Flammèche", "Aquapup", "Sylphin", "Rocoeuf", "Luminette", "Brindibou"]
const MAX_TEAM_SIZE := 3

var player_pos := Vector2(470, 330)
var player_dir := Vector2.DOWN
var hp := 100
var xp := 0
var level := 1
var coins := 500
var eggs := 0
var creatures: Array = []
var team: Array = []
var team_hp: Dictionary = {}
var active_team_index := 0
var battle_active := false
var battle_enemy_index := -1
var battle_enemy_hp := 0
var battle_enemy_max_hp := 0
var battle_label: Label
var battle_panel: PanelContainer
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
	_refresh_ui()
	queue_redraw()

func _draw() -> void:
	# Pixel-art rendering: all world shapes are aligned to a 6 px grid.
	var view := get_viewport_rect().size
	var camera := player_pos - view / 2.0
	camera.x = clampf(camera.x, 0.0, WORLD_SIZE.x - view.x)
	camera.y = clampf(camera.y, 0.0, WORLD_SIZE.y - view.y)
	camera = (camera / 6.0).round() * 6.0
	draw_rect(Rect2(Vector2.ZERO, view), Color("#315d3c"), true)
	var tile := 24
	for x in range(0, int(WORLD_SIZE.x), tile):
		for y in range(0, int(WORLD_SIZE.y), tile):
			var p := Vector2(x, y) - camera
			if p.x < -tile or p.y < -tile or p.x > view.x or p.y > view.y: continue
			var shade := ((x / tile + y / tile) % 2)
			draw_rect(Rect2(p, Vector2(tile, tile)), Color("#477b49") if shade == 0 else Color("#417344"), true)
			if (x * 7 + y * 3) % 11 == 0:
				draw_rect(Rect2(p + Vector2(6, 12), Vector2(6, 6)), Color("#6b9850"), true)
	# Pixel road and village square.
	draw_rect(Rect2(Vector2(0, 432) - camera, Vector2(WORLD_SIZE.x, 96)), Color("#92734d"), true)
	draw_rect(Rect2(Vector2(0, 432) - camera, Vector2(WORLD_SIZE.x, 12)), Color("#b18d5d"), true)
	draw_rect(Rect2(Vector2(348, 228) - camera, Vector2(360, 288)), Color("#765b42"), true)
	for x in range(360, 700, 24):
		for y in range(240, 510, 24):
			draw_rect(Rect2(Vector2(x, y) - camera, Vector2(20, 20)), Color("#a98a5c"), true)
	# Pixel pond.
	draw_rect(Rect2(Vector2(1110, 120) - camera, Vector2(300, 192)), Color("#234d73"), true)
	draw_rect(Rect2(Vector2(1122, 132) - camera, Vector2(276, 168)), Color("#3986a0"), true)
	for i in range(8):
		draw_rect(Rect2(Vector2(1140 + i * 30, 150 + (i % 3) * 42) - camera, Vector2(18, 6)), Color("#72b9bd"), true)
	# Trees built from square pixels.
	for i in range(54):
		var tree_pos := Vector2(80.0 + float((i * 173) % 2050), 100.0 + float((i * 97) % 1200))
		var p := tree_pos - camera
		if p.x < -42 or p.y < -48 or p.x > view.x + 42 or p.y > view.y + 48: continue
		draw_rect(Rect2(p + Vector2(-6, 12), Vector2(12, 24)), Color("#69452e"), true)
		draw_rect(Rect2(p + Vector2(-24, -12), Vector2(48, 30)), Color("#214e35"), true)
		draw_rect(Rect2(p + Vector2(-18, -24), Vector2(36, 30)), Color("#34764a"), true)
		draw_rect(Rect2(p + Vector2(-6, -30), Vector2(18, 12)), Color("#559454"), true)
	# Eggs: pixel silhouettes and highlights.
	for i in range(EGG_POINTS.size()):
		if collected_eggs.has(i): continue
		var ep: Vector2 = EGG_POINTS[i] - camera
		if ep.x < -24 or ep.y < -24 or ep.x > view.x + 24 or ep.y > view.y + 24: continue
		draw_rect(Rect2(ep + Vector2(-6, -18), Vector2(12, 6)), Color("#fff0b0"), true)
		draw_rect(Rect2(ep + Vector2(-12, -12), Vector2(24, 24)), Color("#e7bd58"), true)
		draw_rect(Rect2(ep + Vector2(-6, 12), Vector2(12, 6)), Color("#b87d43"), true)
		draw_rect(Rect2(ep + Vector2(-6, -6), Vector2(6, 6)), Color("#fff2c2"), true)
	# Guide NPC.
	var npc := Vector2(520, 390) - camera
	draw_rect(Rect2(npc + Vector2(-12, -2), Vector2(24, 24)), Color("#a36b35"), true)
	draw_rect(Rect2(npc + Vector2(-9, -17), Vector2(18, 18)), Color("#e6c49b"), true)
	draw_rect(Rect2(npc + Vector2(-12, -23), Vector2(24, 8)), Color("#e0b64d"), true)
	draw_string(ThemeDB.fallback_font, npc + Vector2(-30, -30), "GUIDE", HORIZONTAL_ALIGNMENT_LEFT, -1, 10, Color.WHITE)
	# Wild slimes, made only of square pixels.
	for enemy in enemies:
		if not enemy.alive: continue
		var ep: Vector2 = enemy.pos - camera
		if ep.x < -30 or ep.y < -30 or ep.x > view.x + 30 or ep.y > view.y + 30: continue
		draw_rect(Rect2(ep + Vector2(-18, -6), Vector2(36, 24)), Color("#39884c"), true)
		draw_rect(Rect2(ep + Vector2(-12, -12), Vector2(24, 6)), Color("#62c76b"), true)
		draw_rect(Rect2(ep + Vector2(-12, 0), Vector2(6, 6)), Color("#183c2b"), true)
		draw_rect(Rect2(ep + Vector2(6, 0), Vector2(6, 6)), Color("#183c2b"), true)
		draw_rect(Rect2(ep + Vector2(-18, 22), Vector2(36, 6)), Color("#432d32"), true)
		draw_rect(Rect2(ep + Vector2(-18, 22), Vector2(36.0 * float(enemy.hp) / 45.0, 6)), Color("#e76d5b"), true)
	# Player sprite: blocky adventurer, no vector circles.
	var pp := player_pos - camera
	draw_rect(Rect2(pp + Vector2(-12, 12), Vector2(24, 6)), Color("#24372c"), true)
	draw_rect(Rect2(pp + Vector2(-9, -3), Vector2(18, 21)), Color("#397cb5"), true)
	draw_rect(Rect2(pp + Vector2(-12, -15), Vector2(24, 15)), Color("#e4bd91"), true)
	draw_rect(Rect2(pp + Vector2(-12, -21), Vector2(24, 6)), Color("#71452f"), true)
	draw_rect(Rect2(pp + Vector2(-6, -9), Vector2(6, 3)), Color("#25303a"), true)

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

	battle_panel = PanelContainer.new()
	battle_panel.anchor_left = 0.5
	battle_panel.anchor_right = 0.5
	battle_panel.anchor_top = 0.5
	battle_panel.anchor_bottom = 0.5
	battle_panel.offset_left = -190
	battle_panel.offset_right = 190
	battle_panel.offset_top = -150
	battle_panel.offset_bottom = 150
	battle_panel.visible = false
	ui.add_child(battle_panel)
	var battle_box := VBoxContainer.new()
	battle_box.add_theme_constant_override("separation", 8)
	battle_panel.add_child(battle_box)
	var battle_title := Label.new()
	battle_title.text = "COMBAT DE MONSTRES"
	battle_title.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	battle_title.add_theme_font_size_override("font_size", 20)
	battle_box.add_child(battle_title)
	battle_label = Label.new()
	battle_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	battle_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	battle_box.add_child(battle_label)
	var battle_actions := HBoxContainer.new()
	battle_box.add_child(battle_actions)
	var basic_button := Button.new()
	basic_button.text = "ATTAQUE"
	basic_button.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	basic_button.pressed.connect(_battle_attack)
	battle_actions.add_child(basic_button)
	var skill_button := Button.new()
	skill_button.text = "COMPÉTENCE"
	skill_button.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	skill_button.pressed.connect(_battle_skill)
	battle_actions.add_child(skill_button)
	var battle_tools := HBoxContainer.new()
	battle_box.add_child(battle_tools)
	var switch_button := Button.new()
	switch_button.text = "CHANGER"
	switch_button.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	switch_button.pressed.connect(_battle_switch)
	battle_tools.add_child(switch_button)
	var flee_button := Button.new()
	flee_button.text = "FUIR"
	flee_button.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	flee_button.pressed.connect(_battle_flee)
	battle_tools.add_child(flee_button)


func _make_button(parent: Node, caption: String, offset: Vector2, button_size: Vector2) -> Button:
	var b := Button.new()
	b.text = caption
	b.anchor_top = 1.0
	b.anchor_bottom = 1.0
	if caption == "SAUVEGARDER" or caption == "COLLECTION":
		b.anchor_left = 0.0
		b.anchor_right = 0.0
		b.offset_left = offset.x
		b.offset_right = offset.x + button_size.x
	else:
		b.anchor_left = 1.0
		b.anchor_right = 1.0
		b.offset_left = offset.x
		b.offset_right = offset.x + button_size.x
	b.offset_top = offset.y
	b.offset_bottom = offset.y + button_size.y
	b.add_theme_font_size_override("font_size", 15)
	parent.add_child(b)
	return b

func _interact() -> void:
	if battle_active: return
	for i in range(enemies.size()):
		if enemies[i].alive and player_pos.distance_to(enemies[i].pos) < 100.0:
			_start_battle(i)
			return
	for i in range(EGG_POINTS.size()):
		if not collected_eggs.has(i) and player_pos.distance_to(EGG_POINTS[i]) < 82.0:
			collected_eggs.append(i)
			eggs += 1
			coins += 100
			_set_message("Œuf mystérieux récupéré ! +5 pièces. Tu peux maintenant le faire éclore. +35 XP.")
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
	if team.size() < MAX_TEAM_SIZE:
		team.append(creature)
		team_hp[creature] = _creature_stats(creature).hp
		_set_message("Éclosion ! %s rejoint ta collection et ton équipe. +25 XP." % creature)
	else:
		_set_message("Éclosion ! %s rejoint ta collection. Équipe complète (3/3). +25 XP." % creature)
	_add_xp(25)
	_save_game()

func _attack() -> void:
	if battle_active:
		_battle_attack()
		return
	var target_index := -1
	var nearest := 999999.0
	for i in range(enemies.size()):
		var enemy = enemies[i]
		if enemy.alive:
			var distance := player_pos.distance_to(enemy.pos)
			if distance < 115.0 and distance < nearest:
				nearest = distance
				target_index = i
	if target_index == -1:
		_set_message("Approche-toi d'un monstre sauvage puis touche INTERAGIR ou ATTAQUER.")
		return
	_start_battle(target_index)

func _creature_stats(creature: String) -> Dictionary:
	match creature:
		"Mousselin": return {"hp":105, "attack":17, "element":"Nature", "skill":"Ronces vives"}
		"Flammèche": return {"hp":82, "attack":23, "element":"Feu", "skill":"Boule ardente"}
		"Aquapup": return {"hp":112, "attack":16, "element":"Eau", "skill":"Vague pressurisée"}
		"Sylphin": return {"hp":88, "attack":21, "element":"Air", "skill":"Rafale"}
		"Rocoeuf": return {"hp":145, "attack":13, "element":"Terre", "skill":"Éboulement"}
		"Luminette": return {"hp":94, "attack":20, "element":"Lumière", "skill":"Éclat solaire"}
		_: return {"hp":100, "attack":18, "element":"Nature", "skill":"Feuille-lame"}

func _start_battle(enemy_index: int) -> void:
	if team.is_empty():
		_set_message("Fais éclore un compagnon avant de combattre.")
		return
	battle_active = true
	battle_enemy_index = enemy_index
	battle_enemy_max_hp = int(enemies[enemy_index].hp)
	battle_enemy_hp = battle_enemy_max_hp
	active_team_index = 0
	for i in range(team.size()):
		if int(team_hp.get(str(team[i]), _creature_stats(str(team[i])).hp)) > 0:
			active_team_index = i
			break
	battle_panel.visible = true
	_update_battle_ui()
	_set_message("Combat commencé ! Choisis une action.")

func _battle_attack() -> void:
	if not battle_active: return
	var creature := str(team[active_team_index])
	var stats := _creature_stats(creature)
	var damage := int(stats.attack) + level * 2
	battle_enemy_hp = maxi(0, battle_enemy_hp - damage)
	if battle_enemy_hp <= 0:
		_battle_victory()
		return
	_battle_enemy_turn()
	_update_battle_ui()

func _battle_skill() -> void:
	if not battle_active: return
	var creature := str(team[active_team_index])
	var stats := _creature_stats(creature)
	var damage := int(stats.attack * 1.8) + level * 2
	battle_enemy_hp = maxi(0, battle_enemy_hp - damage)
	_set_message("%s utilise %s : %d dégâts !" % [creature, stats.skill, damage])
	if battle_enemy_hp <= 0:
		_battle_victory()
		return
	_battle_enemy_turn()
	_update_battle_ui()

func _battle_enemy_turn() -> void:
	if not battle_active: return
	var creature := str(team[active_team_index])
	var damage := 9 + int(level / 2)
	var remaining := maxi(0, int(team_hp.get(creature, _creature_stats(creature).hp)) - damage)
	team_hp[creature] = remaining
	if remaining <= 0:
		_set_message("%s est K.-O. !" % creature)
		var next_index := -1
		for i in range(team.size()):
			if int(team_hp.get(str(team[i]), 0)) > 0:
				next_index = i
				break
		if next_index == -1:
			_battle_defeat()
			return
		active_team_index = next_index
		_set_message("À toi, %s ! Choisis ton action." % str(team[active_team_index]))
	else:
		_set_message("Le Slime contre-attaque : %d dégâts sur %s." % [damage, creature])

func _battle_switch() -> void:
	if not battle_active or team.size() < 2: return
	for step in range(1, team.size() + 1):
		var candidate := (active_team_index + step) % team.size()
		if int(team_hp.get(str(team[candidate]), 0)) > 0:
			active_team_index = candidate
			_set_message("À toi, %s !" % str(team[active_team_index]))
			_battle_enemy_turn()
			_update_battle_ui()
			return
	_set_message("Aucun autre compagnon disponible.")

func _battle_flee() -> void:
	if not battle_active: return
	battle_active = false
	battle_panel.visible = false
	_set_message("Tu as fui le combat. L'ennemi est toujours là.")

func _battle_victory() -> void:
	var enemy_id := str(enemies[battle_enemy_index].id)
	enemies[battle_enemy_index].alive = false
	defeated_slimes.append(enemy_id)
	coins += 150
	_add_xp(120)
	battle_active = false
	battle_panel.visible = false
	_set_message("Victoire ! +150 pièces et +120 XP.")
	_save_game()

func _battle_defeat() -> void:
	for creature in team:
		team_hp[str(creature)] = _creature_stats(str(creature)).hp
	battle_active = false
	battle_panel.visible = false
	player_pos = Vector2(470, 330)
	_set_message("Équipe K.-O. Le guide a soigné et réanimé tes compagnons.")
	_save_game()

func _update_battle_ui() -> void:
	if not battle_active or battle_label == null: return
	var creature := str(team[active_team_index])
	var stats := _creature_stats(creature)
	var own_hp := int(team_hp.get(creature, stats.hp))
	battle_label.text = "SLIME SAUVAGE\nPV : %d / %d\n\n%s • %s\nPV : %d / %d\nAttaque : %d | %s" % [battle_enemy_hp, battle_enemy_max_hp, creature, stats.element, own_hp, stats.hp, stats.attack, stats.skill]


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
		"coins":coins, "eggs":eggs, "creatures":creatures, "team":team, "team_hp":team_hp, "collected_eggs":collected_eggs,
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
	team.assign(data.get("team", []))
	team_hp = data.get("team_hp", {})
	if team.is_empty() and not creatures.is_empty():
		for creature in creatures:
			if team.size() >= MAX_TEAM_SIZE: break
			team.append(str(creature))
	for creature in team:
		if not team_hp.has(str(creature)):
			team_hp[str(creature)] = _creature_stats(str(creature)).hp
	collected_eggs.assign(data.get("collected_eggs", []))
	defeated_slimes.assign(data.get("defeated_slimes", []))
	quest_claimed = bool(data.get("quest_claimed", false))
	for enemy in enemies:
		if defeated_slimes.has(enemy.id): enemy.alive = false
