package br.dev.allantoledo.psc.util;

public class PaginationUtility {
    public static int getValidOffset(Integer offset) {
        if (offset == null) return 0;
        if (offset < 0) return 0;
        return offset;
    }

    public static int getValidLimit(Integer limit) {
        if (limit == null) return 100;
        if (limit >= 100) return 100;
        if (limit <= 0) return 0;
        return limit;
    }
}
