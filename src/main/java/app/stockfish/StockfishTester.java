package app.stockfish;

import app.stockfish.engine.enums.Option;
import app.stockfish.engine.enums.Query;
import app.stockfish.engine.enums.QueryType;
import app.stockfish.engine.enums.Variant;
import app.stockfish.exceptions.StockfishInitException;

public class StockfishTester {
    public static void main(String[] args) throws StockfishInitException {
        StockfishClient client = new StockfishClient.Builder()
                .setInstances(4)
                .setOption(Option.Threads, "4") // Number of threads that Stockfish will use
                .setOption(Option.Minimum_Thinking_Time, "1000") // Minimum thinking time Stockfish will take
                .setOption(Option.Skill_Level, "10") // Stockfish skill level 0-20
                .setVariant(Variant.BMI2) // Stockfish Variant
                .build();

        for (int i = 0; i < 12; i++)
            client.submit(new Query.Builder(QueryType.Best_Move)
                            .setFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
                            // .setDifficulty(20) Setting this overrides Skill Level option
                            // .setDepth(62) Setting this makes Stockfish search deeper
                            // .setMovetime(1000) Setting this overrides the minimum thinking time
                            .build(),
                            result -> System.out.println(result)); // This is handling the result of the query
    }
}
