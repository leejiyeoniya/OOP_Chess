/* Copyright 2018 David Cai Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.niflheim.stockfish.engine;

import xyz.niflheim.stockfish.engine.enums.Option;
import xyz.niflheim.stockfish.engine.enums.Query;
import xyz.niflheim.stockfish.engine.enums.Variant;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;

import java.io.IOException;
import java.util.List;

class Stockfish extends UCIEngine {

    Stockfish(String path, Variant variant, Option... options) throws StockfishInitException {
        super(path, variant, options);
    }

    String makeMove(Query query) {
        waitForReady();
        sendCommand("position fen " + query.getFen() + " moves " + query.getMove());
        return getFen();
    }

    String getCheckers(Query query) {
        waitForReady();
        sendCommand("position fen " + query.getFen());

        waitForReady();
        sendCommand("d");

        return readLine("Checkers: ").substring(10);
    }

    String getBestMove(Query query) {
        query.normalize();

        if (query.getDifficulty() >= 0) {
            waitForReady();
            sendCommand("setoption name Skill Level value " + query.getDifficulty());
        }

        waitForReady();
        sendCommand("position fen " + query.getFen());

        StringBuilder command = new StringBuilder("go ");

        if (query.getDepth() >= 0)
            command.append("depth ").append(query.getDepth()).append(" ");

        if (query.getMovetime() >= 0)
            command.append("movetime ").append(query.getMovetime());

        waitForReady();
        sendCommand(command.toString());
        return readLine("bestmove").substring(9).split("\\s+")[0];
    }

    String getLegalMoves(Query query) {
        waitForReady();
        sendCommand("position fen " + query.getFen());

        waitForReady();
        sendCommand("go perft 1");

        StringBuilder legal = new StringBuilder();
        List<String> response = readResponse("Nodes");

        for (String line : response)
            if (!line.isEmpty() && !line.contains("Nodes") && line.contains(":"))
                legal.append(line.split(":")[0]).append(" ");

        return legal.toString();
    }

    void close() throws IOException {
        try {
            if (process.isAlive()) {  // 프로세스가 아직 살아있는지 확인
                sendCommand("quit");  // Stockfish 엔진에 종료 명령 전송
                process.waitFor();    // 프로세스가 정상적으로 종료될 때까지 대기
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                output.flush();  // 출력 스트림을 닫기 전에 플러시
                output.close();  // 출력 스트림 닫기
            }
            if (input != null) {
                input.close();  // 입력 스트림 닫기
            }
            if (process != null) {
                process.destroy();  // 프로세스 종료
            }
        }
    }


    private String getFen() {
        waitForReady();
        sendCommand("d");

        return readLine("Fen: ").substring(5);
    }
}
