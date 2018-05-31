package com.alogic.ik.core;

/**
 * LexemePath
 * 
 * @author yyduan
 * @since 1.6.11.32
 */
class LexemePath extends QuickSortSet implements Comparable<LexemePath> {

    //起始位置
    private int pathBegin;
    //结束
    private int pathEnd;
    //词元链的有效字符长度
    private int payloadLength;

    LexemePath() {
        this.pathBegin = -1;
        this.pathEnd = -1;
        this.payloadLength = 0;
    }

    boolean addCrossLexeme(Lexeme lexeme) {
        if (this.isEmpty()) {
            this.addLexeme(lexeme);
            this.pathBegin = lexeme.getBegin();
            this.pathEnd = lexeme.getBegin() + lexeme.getLength();
            this.payloadLength += lexeme.getLength();
            return true;

        } else if (this.checkCross(lexeme)) {
            this.addLexeme(lexeme);
            if (lexeme.getBegin() + lexeme.getLength() > this.pathEnd) {
                this.pathEnd = lexeme.getBegin() + lexeme.getLength();
            }
            this.payloadLength = this.pathEnd - this.pathBegin;
            return true;

        } else {
            return false;

        }
    }

    boolean addNotCrossLexeme(Lexeme lexeme) {
        if (this.isEmpty()) {
            this.addLexeme(lexeme);
            this.pathBegin = lexeme.getBegin();
            this.pathEnd = lexeme.getBegin() + lexeme.getLength();
            this.payloadLength += lexeme.getLength();
            return true;

        } else if (this.checkCross(lexeme)) {
            return false;

        } else {
            this.addLexeme(lexeme);
            this.payloadLength += lexeme.getLength();
            Lexeme head = this.peekFirst();
            this.pathBegin = head.getBegin();
            Lexeme tail = this.peekLast();
            this.pathEnd = tail.getBegin() + tail.getLength();
            return true;

        }
    }

    Lexeme removeTail() {
        Lexeme tail = this.pollLast();
        if (this.isEmpty()) {
            this.pathBegin = -1;
            this.pathEnd = -1;
            this.payloadLength = 0;
        } else {
            this.payloadLength -= tail.getLength();
            Lexeme newTail = this.peekLast();
            this.pathEnd = newTail.getBegin() + newTail.getLength();
        }
        return tail;
    }

    boolean checkCross(Lexeme lexeme) {
        return (lexeme.getBegin() >= this.pathBegin && lexeme.getBegin() < this.pathEnd)
                || (this.pathBegin >= lexeme.getBegin() && this.pathBegin < lexeme.getBegin() + lexeme.getLength());
    }

    int getPathBegin() {
        return pathBegin;
    }

    int getPathEnd() {
        return pathEnd;
    }

    int getPayloadLength() {
        return this.payloadLength;
    }

    int getPathLength() {
        return this.pathEnd - this.pathBegin;
    }


    int getXWeight() {
        int product = 1;
        Cell c = this.getHead();
        while (c != null && c.getLexeme() != null) {
            product *= c.getLexeme().getLength();
            c = c.getNext();
        }
        return product;
    }

    int getPWeight() {
        int pWeight = 0;
        int p = 0;
        Cell c = this.getHead();
        while (c != null && c.getLexeme() != null) {
            p++;
            pWeight += p * c.getLexeme().getLength();
            c = c.getNext();
        }
        return pWeight;
    }

    LexemePath copy() {
        LexemePath theCopy = new LexemePath();
        theCopy.pathBegin = this.pathBegin;
        theCopy.pathEnd = this.pathEnd;
        theCopy.payloadLength = this.payloadLength;
        Cell c = this.getHead();
        while (c != null && c.getLexeme() != null) {
            theCopy.addLexeme(c.getLexeme());
            c = c.getNext();
        }
        return theCopy;
    }

    public int compareTo(LexemePath o) {
        //比较有效文本长度
        if (this.payloadLength > o.payloadLength) {
            return -1;
        } else if (this.payloadLength < o.payloadLength) {
            return 1;
        } else {
            //比较词元个数，越少越好
            if (this.size() < o.size()) {
                return -1;
            } else if (this.size() > o.size()) {
                return 1;
            } else {
                //路径跨度越大越好
                if (this.getPathLength() > o.getPathLength()) {
                    return -1;
                } else if (this.getPathLength() < o.getPathLength()) {
                    return 1;
                } else {
                    //根据统计学结论，逆向切分概率高于正向切分，因此位置越靠后的优先
                    if (this.pathEnd > o.pathEnd) {
                        return -1;
                    } else if (pathEnd < o.pathEnd) {
                        return 1;
                    } else {
                        //词长越平均越好
                        if (this.getXWeight() > o.getXWeight()) {
                            return -1;
                        } else if (this.getXWeight() < o.getXWeight()) {
                            return 1;
                        } else {
                            //词元位置权重比较
                            if (this.getPWeight() > o.getPWeight()) {
                                return -1;
                            } else if (this.getPWeight() < o.getPWeight()) {
                                return 1;
                            }

                        }
                    }
                }
            }
        }
        return 0;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("pathBegin  : ").append(pathBegin).append("\r\n");
        sb.append("pathEnd  : ").append(pathEnd).append("\r\n");
        sb.append("payloadLength  : ").append(payloadLength).append("\r\n");
        Cell head = this.getHead();
        while (head != null) {
            sb.append("lexeme : ").append(head.getLexeme()).append("\r\n");
            head = head.getNext();
        }
        return sb.toString();
    }

}
