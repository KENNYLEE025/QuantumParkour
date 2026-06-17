# Quantum Parkour

## Overview
Quantum Parkour is a feature-rich Minecraft Java Edition plugin designed for **version 1.21.4**. It provides a complete parkour gaming experience with checkpoint systems, level management, database integration, and enhanced visual customization options.

## Features

### Core Gameplay
- **Checkpoint System** - Create and manage checkpoints throughout your parkour courses for player progression tracking
- **Level Creation and Management** - Design, configure, and manage multiple parkour levels with ease
- **Block Event Handlers** - Trigger custom events and mechanics based on block interactions

### Customization & Display
- **Custom RGB and Hex Color Support** - Enhance messages and visual elements with full RGB and hexadecimal color support
- **Database and Debug System** - Robust data persistence with comprehensive debugging tools for troubleshooting

### Social Features
- **Friend Command** - Built-in friend system for Bukkit servers
  - *Note: Bungee plugin version available in a separate repository*

## Download and Installation

### Prerequisites
- **Minecraft Server**: Java Edition 1.21.4 or compatible version
- **Server Type**: 
  - Bukkit-based server (Spigot, Paper, Purpur, etc.) for full feature support
  - Bungee proxy support available for distributed setups
- **Java**: Java 17 or higher recommended

### Installation Steps

1. **Download the Plugin**
   - Download the latest `QuantumParkour.jar` file from the [releases page](https://github.com/KENNYLEE025/QuantumParkour/releases)
   - Alternatively, clone this repository: `git clone https://github.com/KENNYLEE025/QuantumParkour.git`

2. **Place the Plugin**
   - Copy `QuantumParkour.jar` into your server's `plugins/` directory
   - Create the `plugins/` folder if it doesn't exist

3. **Start Your Server**
   - Start or restart your Minecraft server
   - The plugin will generate configuration files on first launch

4. **Verify Installation**
   - Check server console for confirmation messages
   - Run `/quantum help` or `/parkour help` to view available commands
   - Configuration files will be available in `plugins/QuantumParkour/`

### Configuration

After first launch, configure the plugin:
1. Navigate to `plugins/QuantumParkour/config.yml`
2. Customize settings for levels, checkpoints, colors, and database options
3. Restart the server to apply changes

### Database Setup

Quantum Parkour supports external database connectivity:
- Configure database credentials in `plugins/QuantumParkour/database.yml`
- Supported databases: MySQL, SQLite, and PostgreSQL
- The plugin automatically creates required tables on startup

## Running & Usage

### Basic Commands
- `/parkour create <name>` - Create a new parkour level
- `/parkour play <name>` - Start a parkour level
- `/checkpoint set` - Mark a checkpoint at your current location
- `/parkour stats` - View your personal statistics
- `/friend add <player>` - Add a friend (Bukkit only)

### Server Console
Monitor plugin activity through console logs:
- Enable debug mode in config for detailed logging
- Check `plugins/QuantumParkour/debug.log` for troubleshooting

## Troubleshooting

- **Plugin not loading?** Ensure your server version is 1.21.4 or compatible
- **Database errors?** Verify database credentials in `database.yml`
- **Commands not working?** Check player permissions in your permission manager
- **Colors not displaying?** Update to the latest version and verify RGB/Hex syntax

## Support

For issues, feature requests, or contributions:
- Open an issue on the [GitHub repository](https://github.com/KENNYLEE025/QuantumParkour/issues)
- Check existing issues for solutions
- Provide detailed error messages and server logs when reporting bugs

## License

See the LICENSE file in this repository for licensing information.

## Contributing

Contributions are welcome! Please fork this repository and submit pull requests with your improvements.

---

**Note:** The Bungee version of the Friend Command is maintained in a separate repository. Please check the organization for the Bungee plugin if you're running a proxy-based network.
