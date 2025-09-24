package rlshenanigans.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import rlshenanigans.handlers.NPCInvasionHandler;
import rlshenanigans.handlers.NPCSummonHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandRLS implements ICommand {
    @Override
    public String getName() {
        return "rlshenanigans";
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return "/rlshenanigans <callsummon|startinvasion>";
    }
    
    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString("Usage: /rlshenanigans <callsummon|startinvasion>"));
            return;
        }
        
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString("Only players can use this command."));
            return;
        }
        
        EntityPlayer player = (EntityPlayer) sender;
        
        switch (args[0].toLowerCase()) {
            case "callsummon":
                NPCSummonHandler.spawnSummon(player, 100);
                sender.sendMessage(new TextComponentString("§6Phantom summoned."));
                break;
            
            case "startinvasion":
                NPCInvasionHandler.spawnInvader(player, 100);
                sender.sendMessage(new TextComponentString("§cInvasion started."));
                break;
            
            default:
                sender.sendMessage(new TextComponentString("Unknown subcommand: " + args[0]));
        }
    }
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, getName());
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) return Arrays.asList("callsummon", "startinvasion");
        return Collections.emptyList();
    }
    
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
    
    @Override
    public int compareTo(ICommand o) {
        return this.getName().compareTo(o.getName());
    }
}