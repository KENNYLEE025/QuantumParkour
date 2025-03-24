-=- QUANTUMPRACTICE PLUGIN -=- 


-=- Authors -=-
- Diabet
- Nova

-=- Description -=- 
- Practice feature for Minecraft Parkour. Utilizes commands to set their checkpoint to practice a jump or multiple jumps using red dye and diamond NBT tags. Supports Minecraft Java Edition 1.21 on Paper.

- /prac: Stores player's positions into a practice cache and checkpoint cache
    - Practice cache stores the original position of the player upon entering practice mode
    - Checkpoint cache stores the position of the player; changes when the player chooses to set a practice checkpoint with /setcp.
- /setcp: Changes the checkpoint cache (checkpoint position); does not change the practice position (the coords before the player enters practice mode)
- /unprac: Teleports the player back to the position when the player /pracs, and deletes both the practice and checkpoint caches.

- Red Dye: For going back to their checkpoint purposes when the player enters practice mode
- Diamond: For setting a checkpoint while on practice mode (the original corodinates when the player enters practice mode do not change)


-=- Plugin Requirements -=-
- Requires PlaceholderAPI to run properly: https://www.spigotmc.org/resources/placeholderapi.6245/

-=- IDE Setup -=-

If you are using Microsoft VS Code:
- Download and Install VS Code: https://code.visualstudio.com
- Download and Install Java (version 21 recommended): https://www.java.com/download/ie_manual.jsp
    - In File Explorer: Right-click "This PC" > Properties > Advanced system settings > Environment Variables
    - Under System Variables, create a variable: JAVA_HOME
    - Locate JDK 21 (the default location for this Java file should be under C:\Program Files\Java\jdk-21)
    - Add Java 21's bin to the PATH variable (Java 21's default bin folder should be located under C:\Program Files\Java\jdk-21\bin)
    - Make sure that JDK 21 is either moved above the earlier version of JDK if multiple versions of JDK are installed (recommended to be moved at the very top of the path)
    - To confirm successful installation of Java, go to the terminal and type in "java --version". If successful, the terminal should print the Java version (21.x.x)
- Download and Install Gradle: https://gradle.org/install/ 
    - Make sure to download the zip file, extract the contents, and add them to your C:/Program Files directory
    - Add Gradle to your System Enviornment Variables: 
    - In File Explorer: Right-click "This PC" > Properties > Advanced system settings > Environment Variables
    - Under System Variables, find Path, click Edit, and add: "C:\Program Files\gradle-< version >\bin"
    - To confirm successful installation of gradle, go to the terminal and type in "gradle -v". If successful, the terminal should print out the gradle version


-=- Exporting Plugin as .jar File and Acquiring the Java file-=-
- If you are using VS Code, open the terminal and enter the following command: "gradle build"
- The jar file will be under build/libs


-=- Committing and Pushing Changes to the Plugin to GitHub -=-
- Under "source control", select the files that you want to commit
- Select "commit changes"
- Go to the terminal and execute "git push origin < branch-name >"
- NOTE: by default, < branch-name > is main


-=- Importing changes from Git to IDE -=-
- In the VSCode terminal, make sure to be in the correct branch by executing "git branch"
    - If you are not in the correct branch, execute "git checkout < branch-name >" (this is usually where we store updated files since this plugin does not need to branch out; this can change in rare cases)
- Execute "git pull origin < branch-name >
- NOTE: by default, < branch-name > is main

Token: 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
