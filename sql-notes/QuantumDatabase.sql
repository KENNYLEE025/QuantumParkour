-- DO NOT RUN THIS FILE, IT IS FOR REFERENCE ONLY

CREATE TABLE LEVELS (
	id int PRIMARY KEY NOT NULL,
	name VARCHAR(30) NOT NULL,				
	display_name VARCHAR (500) NOT NULL, 	-- Minecraft formatting go brrrr
	uuid VARCHAR(16) NOT NULL,				-- Creator of the course
	difficulty INT,							
	x FLOAT NOT NULL,		
	y FLOAT NOT NULL,
	z FLOAT NOT NULL,
	pitch FLOAT NOT NULL,
	yaw FLOAT NOT NULL,
	world VARCHAR (20) NOT NULL,			
	announce completion bool NOT NULL,
	level_type INT NOT NULL,				-- enum as int
	category INT NOT NULL, 					-- enum as int
	potion_type INT, 						-- enum as int
	potion_potency INT, 					
	practicable bool NOT NULL,
	released DATE NOT NULL,
	max_completions int NOT NULL
	);
	
	
CREATE TABLE PLAYER (
	uuid VARCHAR(50) PRIMARY KEY NOT NULL,
	rankup_rank INT NOT NULL DEFAULT 0,		-- enum as int
	segmented_rank INT NOT NULL DEFAULT 0,	-- enum as int
	qwobbits INT NOT NULL DEFAULT 0			-- currency name, may be changed if we come up with a better name
	

/*
	Utilizing difficulty standards from 1-10
	
	Level type enumerator:
		0	Normal
		1	onlySprint
		2	noSprint
	
	Category enumerator
		0	Normal
		1	Rankup
		2	Segmented
		3	Speedrun
		4	Endurance
		5	Sky

	Potion type enumerator (will be using movement and (maybe) visual potion effects)
		0	No potions
		1	Speed
		2	Slowness
		3	Jump Boost
	
	For main course progression (rankup and segmented), determine if we want to utilize renatus/lc/hpk's or chelcy/ibis's way of progressing
		For the first option, you start at rank I. When you complete rank I, you achieve rank II.
		For the second option, you start at rank zero. When you complete rank I, you achieve rank I.
		
	Rankup rank enumerator - will need names for each; 
	
	Segmented rank enumerator - will need names for each;
*/