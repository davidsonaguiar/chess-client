package com.fnrc.chess;

import com.fnrc.boardgame.BoardException;

public class ChessException extends BoardException {
    public ChessException(String msg) {
        super(msg);
    }
}