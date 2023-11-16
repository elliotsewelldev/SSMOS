package ssm.gamemanagers.teams;

import ssm.gamemanagers.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmashTeam {

    private String team_name;
    private ChatColor team_color;
    private List<Player> players = new ArrayList<Player>();

    public SmashTeam(String team_name, ChatColor team_color) {
        this.team_name = team_name;
        this.team_color = team_color;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public boolean isOnTeam(Player player) {
        return (players.contains(player));
    }

    public String getName() {
        return team_name;
    }

    public ChatColor getColor() {
        return team_color;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getPlayersSortedByLives() {
        // This is less terrible but still forgive my laziness
        List<Player> least_to_greatest = new ArrayList<Player>();
        HashMap<Player, Integer> lives_copy = new HashMap<Player, Integer>();
        for(Player player : players) {
            lives_copy.put(player, GameManager.getLives(player));
        }
        while(!lives_copy.isEmpty()) {
            int min_value = 0;
            Player min_player = null;
            for (Player check : lives_copy.keySet()) {
                if(min_player == null) {
                    min_player = check;
                    min_value = lives_copy.get(check);
                    continue;
                }
                if(lives_copy.get(check) < min_value) {
                    min_player = check;
                    min_value = lives_copy.get(check);
                }
            }
            least_to_greatest.add(min_player);
            lives_copy.remove(min_player);
        }
        return least_to_greatest;
    }

    public int getTeamSize() {
        return players.size();
    }

    public String getPlayerNames() {
        return "";
    }

    public boolean hasAliveMembers() {
        for(Player player : players) {
            if(!GameManager.getAllLives().containsKey(player)) {
                continue;
            }
            if(GameManager.getAllLives().get(player) > 0) {
                return true;
            }
        }
        return false;
    }

}
