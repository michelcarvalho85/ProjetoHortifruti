package model;

public enum Categoria {
    FRUTAS,
    VERDURAS,
    LEGUMES,
    BEBIDAS,
    OUTROS;

    /**
     * Converte strings comuns do frontend para o enum.
     * Aceita singular/plural e variações de maiúsculas/minúsculas.
     * Ex.: "fruta" -> FRUTAS, "verdura" -> VERDURAS, "legume" -> LEGUMES.
     */
    public static Categoria fromStringFlex(String s) {
        if (s == null) return OUTROS;
        String v = s.trim().toUpperCase();

        // aceitar plural direto
        if (v.equals("FRUTAS")) return FRUTAS;
        if (v.equals("VERDURAS")) return VERDURAS;
        if (v.equals("LEGUMES")) return LEGUMES;
        if (v.equals("BEBIDAS")) return BEBIDAS;
        if (v.equals("OUTROS"))  return OUTROS;

        // aceitar singular
        if (v.equals("FRUTA"))   return FRUTAS;
        if (v.equals("VERDURA")) return VERDURAS;
        if (v.equals("LEGUME"))  return LEGUMES;

        return OUTROS;
    }
}
