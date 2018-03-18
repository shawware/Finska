/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.util.Map;

import au.com.shawware.finska.entity.AbstractEntity;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Game;
import au.com.shawware.finska.entity.Match;
import au.com.shawware.finska.entity.Player;

/**
 * Loads the competitions from store and makes them available.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class CompetitionLoader
{
    /* The singleton instance. */
    private static CompetitionLoader sLoader;

    private final IEntityStore<Competition> mCompetitionStore;
    private final IEntityStore<Match> mMatchStore;
    private final IEntityStore<Game> mGameStore;
    private final IEntityStore<Player> mPlayerStore;

    /**
     * Constructs a new loader.
     * 
     * @param root the root directory to use for storage
     */
    private CompetitionLoader(String root)
    {
        PersistenceFactory factory = PersistenceFactory.getFactory(root);

        mCompetitionStore = factory.getStore(Competition.class);
        mMatchStore       = factory.getStore(Match.class);
        mGameStore        = factory.getStore(Game.class);
        mPlayerStore      = factory.getStore(Player.class);
    }

    /**
     * Gets a singleton instance of the loader.
     * 
     * @param root the root directory to use
     * 
     * @return The loader.
     */
    public static synchronized final CompetitionLoader getLoader(String root)
    {
        if (sLoader == null)
        {
            sLoader = new CompetitionLoader(root);
        }
        return sLoader;
    }

    /**
     * Loads all the players.
     * 
     * @return The players.
     * 
     * @throws PersistenceException error loading players
     */
    @SuppressWarnings("boxing")
    public Map<Integer, Player> getPlayers()
        throws PersistenceException
    {
        return mPlayerStore.getAll();
    }

    /**
     * Loads all the competitions and any dependent entities.
     * 
     * @return The competitions, matches and games.
     * 
     * @throws PersistenceException error loading data
     */
    @SuppressWarnings("boxing")
    public Map<Integer, Competition> getCompetitions()
        throws PersistenceException
    {
        Map<Integer, Player> players = getPlayers();
        Map<Integer, Game> games = mGameStore.getAll();
        Map<Integer, Match> matches = mMatchStore.getAll();
        Map<Integer, Competition> competitions = mCompetitionStore.getAll();

        for (Game game : games.values())
        {
            int id = game.getWinnerId();
            if (id != AbstractEntity.DEFAULT_ID)
            {
                if (players.containsKey(id))
                {
                    game.setWinner(players.get(id));
                }
                else
                {
                    throw new PersistenceException("Game " + game.getId() + " refers to non-existent player; " + id);
                }
            }
        }

        for (Match match : matches.values())
        {
            for (Integer id : match.getGameIds())
            {
                if (games.containsKey(id))
                {
                    match.addGame(games.get(id));
                }
                else
                {
                    throw new PersistenceException("Match " + match.getId() + " refers to non-existent game; " + id);
                }
            }
            for (Integer id : match.getPlayersIds())
            {
                if (players.containsKey(id))
                {
                    match.addPlayer(players.get(id));
                }
                else
                {
                    throw new PersistenceException("Match " + match.getId() + " refers to non-existent player; " + id);
                }
            }
        }
        for (Competition competition : competitions.values())
        {
            for (Integer id : competition.getMatchIds())
            {
                if (matches.containsKey(id))
                {
                    competition.addMatch(matches.get(id));
                }
                else
                {
                    throw new PersistenceException("Competition " + competition.getId() + " refers to non-existent match; " + id);
                }
            }
        }
        return competitions;
    }
}
