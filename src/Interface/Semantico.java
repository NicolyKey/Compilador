package Interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Semantico implements Constants {

    private List<String> codigoObjeto = new ArrayList<>();
    private List<String> listaId = new ArrayList<>();
    private String operadorRelacional = "";
    private Stack<String> pilhaTipos = new Stack<>();
    private Stack<String> pilhaRotulos = new Stack<>();
    private String operadorRelacionalAtual;
    private Map<String, Simbolo> tabelaSimbolos = new HashMap<>();
    private int contadorRotulos = 0;
    private Token token;

    public void executeAction(int action, Token token) throws SemanticError {
        switch (action) {
            case 100 ->
                acao100();
            case 101 ->
                acao101();
            case 102 ->
                acao102(token);
            case 103 ->
                acao103(token);
            case 104 ->
                acao104(token);
            case 105 ->
                acao105(token);
            case 106 ->
                acao106(token);
            case 107 ->
                acao107(token);
            case 108 ->
                acao108();
            case 109 ->
                acao109();
            case 110 ->
                acao110();
            case 111 ->
                acao111();
            case 112 ->
                acao112();
            case 113 ->
                acao113();
            case 114 ->
                acao114();
            case 115 ->
                acao115();
            case 116 ->
                acao116();
            case 117 ->
                acao117();
            case 118 ->
                acao118();
            case 119 ->
                acao119();
            case 120 ->
                acao120();
            case 121 ->
                acao121(token);
            case 122 ->
                acao122();
            case 123 ->
                acao123();
            case 124 ->
                acao124();
            case 125 ->
                acao125();
            case 126 ->
                acao126(token);
            case 127 ->
                acao127(token);
            case 128 ->
                acao128(token);
            case 129 ->
                acao129(token);
            case 130 ->
                acao130(token);
            case 131 ->
                acao131();
            default ->
                throw new SemanticError("Ação semântica desconhecida: " + action, token.getPosition());
        }
    }

    public List<String> getCodigoObjeto() {
        return codigoObjeto;
    }

    private void acao100() {
        codigoObjeto.add(".assembly extern mscorlib {}");
        codigoObjeto.add(".assembly _codigoObjeto {}");
        codigoObjeto.add(".module _codigoObjeto.exe");
        codigoObjeto.add(".class public _UNICA {");
        codigoObjeto.add(".method static public void _principal() {");
        codigoObjeto.add(".entrypoint");
    }

    private void acao101() throws SemanticError {
        codigoObjeto.add("ret");
        codigoObjeto.add("} \n }");
    }

    private void acao102(Token token) throws SemanticError {
        for (String id : listaId) {
            if (tabelaSimbolos.containsKey(id)) {
                throw new SemanticError(token.getLexeme() + " já declarado" , token.getPosition());
            }

            String tipoIL = switch (id.substring(0, 2)) {
                case "i_" ->
                    "int64";
                case "f_" ->
                    "float64";
                case "s_" ->
                    "string";
                case "b_" ->
                    "bool";
                default ->
                    throw new SemanticError("Identificador '" + id + "' deve começar com i_, f_, s_ ou b_" , token.getPosition());
            };
            tabelaSimbolos.put(id, new Simbolo(id, tipoIL, false, token.getPosition()));
            codigoObjeto.add(".locals (" + tipoIL + " " + id + ")");

            if (!tabelaSimbolos.containsKey(id)) {
                throw new SemanticError(id + " não declarado", token.getPosition());
            }
        }
        listaId.clear();
    }

    private void acao103(Token token) throws SemanticError {
        String tipoExpressao = pilhaTipos.pop();

        if (tipoExpressao.equals("int64")) {
            codigoObjeto.add("conv.i8");
        }

        for (int i = 0; i < listaId.size() - 1; i++) {
            codigoObjeto.add("dup");
        }

        for (String id : listaId) {
            if (!tabelaSimbolos.containsKey(id)) {
                throw new SemanticError(id + " não declarado", token.getPosition());
            }
            String tipoIdentificador = tabelaSimbolos.get(id).getTipo();
            
//            if (!tipoIdentificador.equals(tipoExpressao)) {
//                throw new SemanticError(
//                        "Tipo incompatível: não é possível atribuir " + tipoExpressao + " a " + tipoIdentificador , token.getPosition()
//                );
//            }
            codigoObjeto.add("stloc " + id);
        }

        listaId.clear();
    }

    private void acao104(Token token) throws SemanticError {
        String id = token.getLexeme();
        listaId.add(id);
    }

    private void acao105(Token token) throws SemanticError {
        String lexeme = token.getLexeme();

         for (String id : listaId) {
            if (!tabelaSimbolos.containsKey(id)) {
                throw new SemanticError(id + " não declarado", token.getPosition());
            }
            String tipoIdentificador = tabelaSimbolos.get(id).getTipo();
            codigoObjeto.add("stloc " + id);
        }

        codigoObjeto.add("call string [mscorlib]System.Console::ReadLine()");

        String tipoIL = switch (lexeme.substring(0, 2)) {
            case "i_" -> {
                yield "call int64 [mscorlib]System.Int64::Parse(string)";
            }
            case "f_" -> {
                yield "call float64 [mscorlib]System.Double::Parse(string)";
            }
            case "s_" -> {
                yield "";
            }
            case "b_" -> {
                yield "call bool [mscorlib]System.Boolean::Parse(string)";
            }
            default ->
                throw new SemanticError("Tipo não suportado para leitura: " + lexeme , token.getPosition());
        };
        if (!tipoIL.isEmpty()) {
            codigoObjeto.add(tipoIL);
        }

        codigoObjeto.add("stloc " + lexeme);
    }

    private void acao106(Token token) throws SemanticError {
        String lexeme = token.getLexeme();
        codigoObjeto.add("ldstr " + lexeme);
        codigoObjeto.add("call void [mscorlib]System.Console::Write(string)");
    }

    private String getTipoFromId(String id) throws SemanticError {
        if (id.startsWith("i_")) {
            return "int64";
        }
        if (id.startsWith("f_")) {
            return "float64";
        }
        if (id.startsWith("s_")) {
            return "string";
        }
        if (id.startsWith("b_")) {
            return "bool";
        }
        throw new SemanticError("Identificador com prefixo inválido: " + id , token.getPosition());
    }

    private void acao107(Token token) throws SemanticError {
        for (String id : listaId) {
            String tipo = pilhaTipos.pop();

            if ("int64".equals(tipo)) {
                codigoObjeto.add("conv.i8");
            }

            switch (tipo) {
                case "int64":
                    codigoObjeto.add("call void [mscorlib]System.Console::WriteLine(int64)");
                    break;
                case "float64":
                    codigoObjeto.add("call void [mscorlib]System.Console::WriteLine(float64)");
                    break;
                case "string":
                    codigoObjeto.add("call void [mscorlib]System.Console::WriteLine(string)");
                    break;
                case "bool":
                    codigoObjeto.add("call void [mscorlib]System.Console::WriteLine(bool)");
                    break;
                default:
                    throw new SemanticError("Tipo inválido para operação de escrita: " + tipo  , token.getPosition());
            }
        }

        for (int i = codigoObjeto.size() - 1; i >= 0; i--) {
            if (codigoObjeto.get(i).startsWith("call void [mscorlib]System.Console::Write(")) {
                String linhaAtual = codigoObjeto.get(i);
                codigoObjeto.set(i, linhaAtual.replace("Write(", "WriteLine("));
                break;
            }
        }
        listaId.clear();
    }

    private void acao108() throws SemanticError {
        String tipo = pilhaTipos.pop();

        if (tipo.equals("int64")) {
            codigoObjeto.add("conv.i8");
        }

        switch (tipo) {
            case "int64":
                codigoObjeto.add("call void [mscorlib]System.Console::Write(int64)");
                break;
            case "float64":
                codigoObjeto.add("call void [mscorlib]System.Console::Write(float64)");
                break;
            case "string":
                codigoObjeto.add("call void [mscorlib]System.Console::Write(string)");
                break;
            case "bool":
                codigoObjeto.add("call void [mscorlib]System.Console::Write(bool)");
                break;
            default:
                throw new SemanticError("Tipo inválido para operação de escrita: " + tipo , token.getPosition());
        }
    }

    private void acao109() throws SemanticError {
        String novoRotulo1 = novoRotulo();
        pilhaRotulos.push(novoRotulo1);
        String novoRotulo2 = novoRotulo();
        codigoObjeto.add(String.format("brfalse %s", novoRotulo2));
        pilhaRotulos.push(novoRotulo2);
    }

    private void acao110() throws SemanticError {
        String rotuloDesempilhado2 = pilhaRotulos.pop();
        String rotuloDesempilhado1 = pilhaRotulos.pop();
        codigoObjeto.add(String.format("br %s", rotuloDesempilhado1));
        pilhaRotulos.push(rotuloDesempilhado1);
        codigoObjeto.add(String.format("%s:", rotuloDesempilhado2));
    }

    private void acao111() throws SemanticError {
        String rotuloDesempilhado = pilhaRotulos.pop();
        codigoObjeto.add(String.format("%s:", rotuloDesempilhado));
    }

    private void acao112() throws SemanticError {
        String novoRotulo = novoRotulo();
        codigoObjeto.add(String.format("brfalse %s", novoRotulo));
        pilhaRotulos.push(novoRotulo);
    }

    private void acao113() {
        String novoRotulo = novoRotulo();
        codigoObjeto.add(String.format("%s:", novoRotulo));
        pilhaRotulos.push(novoRotulo);
    }

    private void acao114() {
        String rotuloDesempilhado = pilhaRotulos.pop();
        codigoObjeto.add(String.format("brtrue %s", rotuloDesempilhado));
    }

    private void acao115() throws SemanticError {
        String rotuloDesempilhado = pilhaRotulos.pop();
        codigoObjeto.add(String.format("brfalse %s", rotuloDesempilhado));
    }

    private String novoRotulo() {
        return "rotulo" + contadorRotulos++;
    }

    private void comparacaoTipos(String operador) throws SemanticError {
        String tipo1 = pilhaTipos.pop();
        String tipo2 = pilhaTipos.pop();

        empilhaCombinacao(tipo1, tipo2, operador);
    }

    private void empilhaCombinacao(String tipo1, String tipo2, String operador)
            throws SemanticError {
        switch (operador) {
            case "+":
            case "-":
            case "*":
            case "/":
                combinacaoAritmetica(tipo1, tipo2);
                break;
            case "&&":
            case "||":
                combinacaoOU(tipo1, tipo2);
                break;
            case "==":
            case "!=":
            case "<":
            case ">":
                combinacaoMaiorQue(tipo1, tipo2);
                break;
            default:
                throw new SemanticError("operador inválido" , token.getPosition());

        }
    }

    private void combinacaoOU(String firstValueType, String secondValueType) throws SemanticError {
        if (!firstValueType.equals("bool") || !secondValueType.equals("bool")) {
            throw new SemanticError("Tipos incompatíveis" , token.getPosition());
        }
        empilhaBooleano();
    }

    private void combinacaoMaiorQue(String tipo1, String tipo2) throws SemanticError {
        if (validacaoOperacao(tipo1, tipo2)) {
            throw new SemanticError("Tipos incompatíveis" , token.getPosition());
        }
        empilhaBooleano();
    }

    private boolean validacaoOperacao(String tipo1, String tipo2) {
        return (tipo1 != tipo2)
                || (!tipo1.equals("int64") && !tipo1.equals("float64") && !tipo1.equals("string"))
                || (!tipo2.equals("int64") && !tipo2.equals("float64") && !tipo2.equals("string"));
    }

    private void empilhaBooleano() {
        pilhaTipos.push("bool");
    }

    private void combinacaoAritmetica(String tipo1, String tipo2) throws SemanticError {
        if (tipo1 == tipo2) {
            pilhaTipos.push(tipo1);
            return;
        } else if (tipo1.equals("int64") && tipo2.equals("float64")
                || tipo1.equals("float64") && tipo2.equals("int64")) {
            pilhaTipos.push("float64");
            return;
        }

        throw new SemanticError("Tipos incompatíveis" , token.getPosition());
    }

    private void acao116() throws SemanticError {
        comparacaoTipos("&&");
        codigoObjeto.add("and");
    }

    private void acao117() throws SemanticError {
        comparacaoTipos("||");
        codigoObjeto.add("or");
    }

    private void acao118() {
        pilhaTipos.push("bool");
        codigoObjeto.add("ldc.i4.1\n");
    }

    private void acao119() throws SemanticError {
        pilhaTipos.push("bool");
        codigoObjeto.add("ldc.i4.0\n");
    }

    private void acao120() {
        codigoObjeto.add("ldc.i8 1\nxor\n");
    }

    private void acao121(Token token) throws SemanticError {
        operadorRelacionalAtual = token.getLexeme();
    }

    private void acao122() throws SemanticError {
        comparacaoTipos(operadorRelacionalAtual);

        switch (operadorRelacionalAtual) {
            case "==":
                codigoObjeto.add("ceq\n");
                break;
            case "!=":
                codigoObjeto.add("ceq\n");
                codigoObjeto.add("ldc.i4 1\nxor\n");
                break;
            case "<":
                codigoObjeto.add("clt\n");
                break;
            case ">":
                codigoObjeto.add("cgt\n");
                break;
            default:
                throw new SemanticError("Operador relacional desconhecido: " + operadorRelacionalAtual , token.getPosition());
        }
    }

    private void acao123() throws SemanticError {
        comparacaoTipos("+");
        codigoObjeto.add("add");
    }

    private void acao124() throws SemanticError {
        comparacaoTipos("-");
        codigoObjeto.add("sub");
    }

    private void acao125() throws SemanticError {
        comparacaoTipos("*");
        codigoObjeto.add("mul");
    }

    private void acao126(Token token) throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        if (!tipo1.equals(tipo2)) {
            throw new SemanticError("Tipos incompatíveis para a operação de divisão. Ambos os operandos devem ser do mesmo tipo.", token.getPosition());
        }

        if (!tipo1.equals("int64") && !tipo1.equals("float64")) {
            throw new SemanticError("Operação de divisão suportada apenas para tipos numéricos (int64 ou float64)." , token.getPosition());
        }

        codigoObjeto.add("div");
        pilhaTipos.push(tipo1);
    }

    private String pegarTipoId(String id) throws SemanticError {
        String prefixo = id.split("_")[0];
        switch (prefixo) {
            case "i":
                return "int64";
            case "f":
                return "float64";
            case "s":
                return "string";
            case "b":
                return "bool";
            default:
                throw new SemanticError("Identificador de variável incorreto: " + id, token.getPosition());
        }
    }

    private void acao127(Token token) throws SemanticError {
        String lexeme = token.getLexeme();

        if (!tabelaSimbolos.containsKey(lexeme)) {
            throw new SemanticError(lexeme + " não declarado" , token.getPosition());
        }
        String varType = pegarTipoId(lexeme);
        pilhaTipos.push(varType);

        codigoObjeto.add("ldloc " + lexeme);

        if ("int64".equals(varType)) {
            codigoObjeto.add("conv.r8");
        }
    }

    private void acao128(Token token) {
        pilhaTipos.push("int64");
        codigoObjeto.add("ldc.i8 " + token.getLexeme());
        codigoObjeto.add("conv.r8");
    }

    private void acao129(Token token) {
        pilhaTipos.push("float64");
        String valor = token.getLexeme().replace(",", ".");
        codigoObjeto.add("ldc.r8 " + valor);
    }

    private void acao130(Token token) throws SemanticError {
        if (token != null) {
            pilhaTipos.push("string");
            codigoObjeto.add("ldstr " + token.getLexeme());
        } else {
            pilhaTipos.push("string");
            codigoObjeto.add("ldstr \"erro\"");
        }
    }

    private void acao131() {
        codigoObjeto.add("ldc.r8 -1.0");
        codigoObjeto.add("mul");
    }

}
