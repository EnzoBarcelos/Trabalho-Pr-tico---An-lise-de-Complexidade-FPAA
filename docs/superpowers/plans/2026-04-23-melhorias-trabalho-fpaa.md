# Melhorias Trabalho FPAA — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Elevar o trabalho prático FPAA com validação automática, rigor estatístico (5 seeds + desvio padrão) e texto humanizado no artigo LaTeX.

**Architecture:** Mudanças incrementais — primeiro o código Java (assert + variância + CSV), depois execução do benchmark, depois Python lendo o CSV e regerando gráficos, por fim atualização das tabelas e humanização do texto do `artigo.tex`. Sem novas variantes de DSU; sem mudar estrutura do artigo.

**Tech Stack:** Java 24, Python 3.11 (matplotlib, numpy), LaTeX (sbc-template).

---

## Task 1: Validação automática em `exemploKruskal()`

**Files:**
- Modify: `src/Main.java` (método `exemploKruskal`, linhas 49-88)

- [ ] **Step 1: Substituir corpo de `exemploKruskal()`**

Trocar o método pela versão com validação:

```java
private static void exemploKruskal() {
    System.out.println();
    System.out.println("--- Exemplo: Grafo com 6 vertices ---");

    List<Aresta> arestas = new ArrayList<>();
    arestas.add(new Aresta(0, 1, 4));
    arestas.add(new Aresta(0, 3, 1));
    arestas.add(new Aresta(1, 2, 2));
    arestas.add(new Aresta(1, 4, 3));
    arestas.add(new Aresta(2, 5, 5));
    arestas.add(new Aresta(3, 4, 6));
    arestas.add(new Aresta(4, 5, 7));

    DSU[] variantes = { new DSUNaive(), new DSURank(), new DSUTarjan() };

    double pesoReferencia = Double.NaN;

    for (DSU dsu : variantes) {
        List<Aresta> copia = new ArrayList<>(arestas);

        Kruskal.ResultadoKruskal resultado =
                Kruskal.executarDetalhado(6, copia, dsu);

        System.out.println();
        System.out.println("Variante: " + dsu.getNome());
        System.out.println("Peso total da MST: " + resultado.pesoTotal);
        System.out.println("Operacoes de acesso: " + resultado.operacoes);
        System.out.print("Arestas da MST: ");
        for (Aresta a : resultado.arestas) {
            System.out.print(a + "  ");
        }
        System.out.println();

        if (Double.isNaN(pesoReferencia)) {
            pesoReferencia = resultado.pesoTotal;
        } else if (Math.abs(pesoReferencia - resultado.pesoTotal) > 1e-9) {
            throw new AssertionError(
                "Peso divergente para " + dsu.getNome() + ": " +
                resultado.pesoTotal + " (esperado " + pesoReferencia + ")"
            );
        }
    }

    System.out.println();
    System.out.println("Validacao OK: as 3 variantes concordam no peso da MST.");
}
```

- [ ] **Step 2: Compilar**

Run: `javac -d out src/Main.java src/dsu/*.java src/grafo/*.java src/benchmark/*.java`
Expected: sem erros.

- [ ] **Step 3: Executar opção 1 e verificar mensagem de validação**

Run: `echo -e "1\n0" | java -cp out Main`
Expected: output com três blocos (Naive, Union by Rank, Tarjan com o mesmo peso) e, ao final, a linha `Validacao OK: as 3 variantes concordam no peso da MST.`

- [ ] **Step 4: Commit**

```bash
git add src/Main.java
git commit -m "feat(main): valida peso identico da MST entre as tres variantes"
```

---

## Task 2: Variância e saída CSV em `Benchmark.executarDetalhado()`

**Files:**
- Modify: `src/benchmark/Benchmark.java` (substituir método `executarDetalhado`, adicionar helpers `media` e `desvioPadrao`, adicionar imports)

- [ ] **Step 1: Adicionar imports no topo**

No topo do `Benchmark.java`, adicionar (depois dos imports existentes):

```java
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
```

- [ ] **Step 2: Substituir método `executarDetalhado()` inteiro**

Substituir o método pela versão com arrays, desvio padrão e gravação de CSV:

```java
public static void executarDetalhado() {
    System.out.println("=== BENCHMARK DETALHADO (media de 5 execucoes, seeds distintas) ===");
    System.out.println();

    DSU[] warmup = { new DSUNaive(), new DSURank(), new DSUTarjan() };
    List<Aresta> warmupGrafo = gerarGrafoAleatorio(5000, 15000, 99L);
    for (DSU dsu : warmup) {
        Kruskal.executar(5000, new ArrayList<>(warmupGrafo), dsu);
    }

    long[] seeds = {42L, 43L, 44L, 45L, 46L};

    System.out.printf("%-10s %-8s %-30s %-22s %-25s%n",
            "n", "m", "Variante", "Tempo (ms)", "Ops");
    System.out.println("-".repeat(100));

    List<String> linhasCsv = new ArrayList<>();
    linhasCsv.add("n,m,variante,tempo_medio,tempo_std,ops_media,ops_std");

    for (int n : TAMANHOS) {
        int m = n * 3;

        DSU[] variantes = { new DSUNaive(), new DSURank(), new DSUTarjan() };

        for (DSU dsu : variantes) {
            double[] tempos = new double[seeds.length];
            double[] ops = new double[seeds.length];

            for (int i = 0; i < seeds.length; i++) {
                List<Aresta> arestas = gerarGrafoAleatorio(n, m, seeds[i]);
                dsu.resetContador();

                long inicio = System.nanoTime();
                Kruskal.executar(n, arestas, dsu);
                long fim = System.nanoTime();

                tempos[i] = (fim - inicio) / 1_000_000.0;
                ops[i] = dsu.getContador();
            }

            double tempoMedio = media(tempos);
            double tempoStd = desvioPadrao(tempos, tempoMedio);
            double opsMedia = media(ops);
            double opsStd = desvioPadrao(ops, opsMedia);

            System.out.printf(Locale.US,
                    "%-10d %-8d %-30s %8.2f +/- %-8.2f  %14.0f +/- %-10.0f%n",
                    n, m, dsu.getNome(), tempoMedio, tempoStd, opsMedia, opsStd);

            linhasCsv.add(String.format(Locale.US, "%d,%d,%s,%.4f,%.4f,%.2f,%.2f",
                    n, m, dsu.getNome(), tempoMedio, tempoStd, opsMedia, opsStd));
        }
        System.out.println();
    }

    try (PrintWriter csv = new PrintWriter(new FileWriter("benchmark_detalhado.csv"))) {
        for (String linha : linhasCsv) csv.println(linha);
    } catch (IOException e) {
        System.err.println("Falha ao gravar CSV: " + e.getMessage());
        return;
    }
    System.out.println("CSV gravado em benchmark_detalhado.csv (" + (linhasCsv.size() - 1) + " linhas)");
}

private static double media(double[] valores) {
    double s = 0;
    for (double v : valores) s += v;
    return s / valores.length;
}

private static double desvioPadrao(double[] valores, double media) {
    if (valores.length < 2) return 0.0;
    double soma = 0;
    for (double v : valores) {
        double d = v - media;
        soma += d * d;
    }
    return Math.sqrt(soma / (valores.length - 1));
}
```

- [ ] **Step 3: Compilar**

Run: `javac -d out src/Main.java src/dsu/*.java src/grafo/*.java src/benchmark/*.java`
Expected: sem erros.

- [ ] **Step 4: Executar opção 3 e verificar formato**

Run: `echo -e "3\n0" | java -cp out Main`
Expected: tabela legível no stdout com colunas formato `8.42 +/- 0.35` e `2156789 +/- 14523`, seguida de mensagem "CSV gravado em benchmark_detalhado.csv (15 linhas)". Pode demorar ~2 minutos (Naive em n=10^5 roda 5 vezes).

- [ ] **Step 5: Validar CSV gerado**

Ler `benchmark_detalhado.csv`. Deve ter 16 linhas (1 cabeçalho + 15 dados), cabeçalho `n,m,variante,tempo_medio,tempo_std,ops_media,ops_std`, valores com ponto decimal.

- [ ] **Step 6: Commit**

```bash
git add src/benchmark/Benchmark.java benchmark_detalhado.csv
git commit -m "feat(benchmark): media e desvio padrao sobre 5 seeds + saida CSV"
```

---

## Task 3: `gerar_graficos.py` lendo CSV com barras de erro

**Files:**
- Modify: `gerar_graficos.py` (substituir parsing hardcoded, adicionar errorbars em gráficos de tempo)

- [ ] **Step 1: Substituir cabeçalho do script**

Trocar as linhas 1-46 (import, dados hardcoded, parsing) por:

```python
import csv
import sys
import matplotlib.pyplot as plt
import numpy as np

# Uso: python gerar_graficos.py [caminho_csv]
# Default: benchmark_detalhado.csv na pasta atual

caminho_csv = sys.argv[1] if len(sys.argv) > 1 else "benchmark_detalhado.csv"

dados = {"Naive": {}, "Union by Rank": {}, "Tarjan (Rank + Compressao)": {}}

with open(caminho_csv, newline="", encoding="utf-8") as f:
    leitor = csv.DictReader(f)
    for linha in leitor:
        variante = linha["variante"]
        n = int(linha["n"])
        registro = {
            "tempo_medio": float(linha["tempo_medio"]),
            "tempo_std":   float(linha["tempo_std"]),
            "ops_media":   float(linha["ops_media"]),
            "ops_std":     float(linha["ops_std"]),
        }
        dados[variante][n] = registro

def extrair(variante, campo):
    itens = sorted(dados[variante].items())
    ns = [n for n, _ in itens]
    vals = [r[campo] for _, r in itens]
    return ns, vals

naive_n,  naive_tempo    = extrair("Naive", "tempo_medio")
_,        naive_tempo_s  = extrair("Naive", "tempo_std")
_,        naive_ops      = extrair("Naive", "ops_media")

rank_n,   rank_tempo     = extrair("Union by Rank", "tempo_medio")
_,        rank_tempo_s   = extrair("Union by Rank", "tempo_std")
_,        rank_ops       = extrair("Union by Rank", "ops_media")

tarjan_n, tarjan_tempo   = extrair("Tarjan (Rank + Compressao)", "tempo_medio")
_,        tarjan_tempo_s = extrair("Tarjan (Rank + Compressao)", "tempo_std")
_,        tarjan_ops     = extrair("Tarjan (Rank + Compressao)", "ops_media")

plt.rcParams['figure.figsize'] = (10, 6)
plt.rcParams['font.size'] = 11
```

- [ ] **Step 2: Verificar que o nome da variante bate com o Java**

O CSV é escrito pelo Java chamando `dsu.getNome()`. Em `DSUTarjan.java:66` está `"Tarjan (Rank + Compressao)"` (sem til no "ã"). O script Python acima usa exatamente essa string — confirmar.

Run: `grep "Compressao" benchmark_detalhado.csv | head -1`
Expected: linha contendo `Tarjan (Rank + Compressao)`.

- [ ] **Step 3: Adicionar errorbar nos gráficos de tempo**

Localizar o bloco do **GRAFICO 3** (tempo todas) e substituir as três linhas `ax.plot(...)` por `ax.errorbar(...)`:

```python
ax.errorbar(naive_n,  naive_tempo,  yerr=naive_tempo_s,
            fmt='ro-', label='Naive', linewidth=2, markersize=7, capsize=4)
ax.errorbar(rank_n,   rank_tempo,   yerr=rank_tempo_s,
            fmt='bs-', label='Union by Rank', linewidth=2, markersize=7, capsize=4)
ax.errorbar(tarjan_n, tarjan_tempo, yerr=tarjan_tempo_s,
            fmt='g^-', label='Tarjan (Rank + Compressão)', linewidth=2, markersize=7, capsize=4)
```

Fazer o mesmo no bloco do **GRAFICO 4** (tempo Rank vs Tarjan), só com as duas curvas otimizadas:

```python
ax.errorbar(rank_n,   rank_tempo,   yerr=rank_tempo_s,
            fmt='bs-', label='Union by Rank', linewidth=2, markersize=7, capsize=4)
ax.errorbar(tarjan_n, tarjan_tempo, yerr=tarjan_tempo_s,
            fmt='g^-', label='Tarjan (Rank + Compressão)', linewidth=2, markersize=7, capsize=4)
```

- [ ] **Step 4: Deixar gráficos de operações e expoente como estão**

Não alterar blocos de GRAFICO 1, 2, 5, 6, 7 — continuam usando `ax.plot(...)` com as médias. A variância nas operações é pequena (mesma algoritmia com seeds diferentes variam sobretudo em topologia do grafo) e barras de erro ali poluem.

- [ ] **Step 5: Executar**

Run: `python gerar_graficos.py benchmark_detalhado.csv`
Expected: 7 mensagens "Salvo: grafico_...png" e "Todos os gráficos foram gerados com sucesso!"

- [ ] **Step 6: Verificar PNGs regerados**

Run: `ls -la grafico_*.png`
Expected: 7 arquivos PNG com timestamp recente.

- [ ] **Step 7: Commit**

```bash
git add gerar_graficos.py grafico_*.png
git commit -m "refactor(graficos): leitura de CSV e barras de erro nos tempos"
```

---

## Task 4: Atualizar tabelas do artigo com novos valores

**Files:**
- Modify: `artigo.tex` (Tabelas 2 e 3, subseções de Resultados com números específicos)

- [ ] **Step 1: Ler CSV gerado**

Abrir `benchmark_detalhado.csv` e anotar os 15 registros. Serão a fonte de verdade das tabelas.

- [ ] **Step 2: Atualizar Tabela de Operações (`tab:operacoes`, linhas 184-199)**

Substituir os valores da coluna por `ops_media` do CSV arredondado para inteiro. O formato da tabela continua o mesmo (sem ±, pois a variância de ops é pequena e não informativa).

Em caso de dúvida, manter apenas a coluna de média e não adicionar ±σ.

- [ ] **Step 3: Atualizar Tabela de Tempo (`tab:tempo`, linhas 225-239)**

Adicionar coluna de desvio padrão. Mudar o formato para `média ± σ` usando `$\pm$`. Exemplo de célula nova: `89,33 $\pm$ 2,14`.

Layout proposto:
```latex
\begin{tabular}{|r|r|r|r|}
\hline
\textbf{n} & \textbf{Naive (ms)} & \textbf{Rank (ms)} & \textbf{Tarjan (ms)} \\
\hline
1.000      & 1,49 $\pm$ 0,12      & 1,23 $\pm$ 0,08   & 0,76 $\pm$ 0,05    \\
...
\end{tabular}
```

Valores exatos virão do CSV.

- [ ] **Step 4: Atualizar Tabela de Expoente Empírico (`tab:expoente`, linhas 266-280)**

Recalcular os expoentes `p = log(ops_{i+1}/ops_i) / log(n_{i+1}/n_i)` usando as novas médias. Ajustar a tabela se os valores mudarem (provavelmente mudam pouco — o crescimento é robusto).

- [ ] **Step 5: Atualizar citações numéricas breves que cabem nas tabelas**

Apenas citações embutidas nas legendas/texto imediato das tabelas. Citações mais longas em parágrafos de análise ficam para as Tasks 6 e 7 (que vão reescrever os parágrafos e já vão usar os números atualizados).

Concretamente: se alguma legenda de figura cita número (ex: "redução de três ordens de grandeza"), verificar e ajustar.

- [ ] **Step 6: Commit**

```bash
git add artigo.tex
git commit -m "docs(artigo): tabelas e comentarios com medias de 5 seeds e desvio padrao"
```

---

## Task 5: Atualizar Metodologia e Limitações do artigo

**Files:**
- Modify: `artigo.tex` (subseção "Cenários de Teste" linha 158-160, subseção "Ambiente de Execução" linha 174, seção "Limitações" linha 319)

- [ ] **Step 1: Atualizar Cenários de Teste**

Substituir o parágrafo atual (linha 160):

Antes:
```
Os testes foram realizados para cinco tamanhos de entrada: $n \in \{1.000;\; 5.000;\; 10.000;\; 50.000;\; 100.000\}$. Para cada configuração, o Kruskal foi executado com as três variantes de DSU, medindo-se o tempo de execução via \texttt{System.nanoTime()} e a contagem de acessos ao array de pais.
```

Depois:
```
Os testes foram realizados para cinco tamanhos de entrada: $n \in \{1.000;\; 5.000;\; 10.000;\; 50.000;\; 100.000\}$. Para cada configuração, o Kruskal foi executado com as três variantes de DSU em cinco grafos distintos (seeds $42$--$46$), medindo-se o tempo de execução via \texttt{System.nanoTime()} e a contagem de acessos ao array de pais. Os valores reportados nas tabelas são a média das cinco repetições; para os tempos, também é reportado o desvio padrão amostral.
```

- [ ] **Step 2: Atualizar Ambiente de Execução (linha 174)**

Substituir o parágrafo atual:

Antes:
```
Antes de cada rodada de medição, uma fase de \textit{warm-up} executa as três variantes em um grafo auxiliar de $n = 5.000$ vértices (descartado). Isso permite que o compilador JIT compile os caminhos quentes antes das medições oficiais, reduzindo o impacto da compilação em tempo de execução nos tempos registrados. Cada ponto das tabelas corresponde a uma única execução com semente fixa 42; os contadores de acesso, por serem determinísticos, não são afetados por variância de execução.
```

Depois:
```
Antes de cada rodada de medição, uma fase de \textit{warm-up} executa as três variantes em um grafo auxiliar de $n = 5.000$ vértices (descartado). Isso permite que o compilador JIT compile os caminhos quentes antes das medições oficiais, reduzindo o impacto da compilação em tempo de execução nos tempos registrados. Cada ponto das tabelas corresponde à média de cinco execuções em grafos diferentes (gerados com as seeds $42, 43, 44, 45$ e $46$). O desvio padrão reportado para os tempos é calculado com estimador amostral ($n-1$ no denominador).
```

- [ ] **Step 3: Atualizar seção Limitações (linha 319)**

Antes:
```
Os tempos medidos correspondem a execuções únicas com semente fixa; variância amostral não foi estimada. Além disso, a topologia dos grafos gerados (árvore-caminho com arestas extras) não representa a distribuição uniforme de um grafo aleatório, o que pode influenciar a profundidade média das árvores DSU. Os resultados devem ser interpretados como evidência de tendências assintóticas, não como medidas de desempenho absolutas.
```

Depois:
```
A topologia dos grafos gerados (árvore-caminho com arestas extras) não representa a distribuição uniforme de um grafo de Erd\H{o}s--R\'enyi, o que pode influenciar a profundidade média das árvores DSU. O intervalo de $n$ investigado vai até $10^5$; extrapolações para escalas maiores assumem que o regime assintótico já foi atingido. Os resultados devem ser lidos como evidência de tendências de complexidade, não como benchmarks absolutos de desempenho.
```

- [ ] **Step 4: Commit**

```bash
git add artigo.tex
git commit -m "docs(artigo): metodologia e limitacoes refletem o setup de 5 seeds"
```

---

## Task 6: Humanização do artigo — travessões e paralelismos

**Files:**
- Modify: `artigo.tex` (passagens específicas, listadas abaixo)

Contexto: o texto atual tem 11 ocorrências de `---` (travessão LaTeX). A memória do usuário sinaliza travessões em excesso como marcador típico de IA. Meta: ficar em no máximo 3 travessões no artigo todo, e só em lugares onde um parêntese ou vírgula não cabe.

Além disso, quebrar as construções mais obviamente paralelas (anáfora "Para X... Para Y... Para Z" e "Quanto à primeira... Quanto à segunda").

- [ ] **Step 1: Introdução, linha 41**

Antes:
```
...reduzem a complexidade amortizada para $O(\alpha(n))$, onde $\alpha$ é a inversa da função de Ackermann --- uma função que cresce tão lentamente que, para qualquer valor prático de $n$, seu resultado não ultrapassa 4 \cite{tarjan1975}.
```

Depois:
```
...reduzem a complexidade amortizada para $O(\alpha(n))$, onde $\alpha$ é a inversa da função de Ackermann. Essa função cresce tão lentamente que, para qualquer valor prático de $n$, seu resultado não ultrapassa 4 \cite{tarjan1975}.
```

- [ ] **Step 2: Fundamentação, linha 79**

Antes:
```
...de modo que seu custo real alto é compensado por uma queda equivalente no potencial. O custo amortizado por operação --- custo real mais variação de potencial --- fica limitado a $O(\alpha(n))$.
```

Depois:
```
...de modo que seu custo real alto é compensado por uma queda equivalente no potencial. O custo amortizado por operação (custo real mais variação de potencial) fica limitado a $O(\alpha(n))$.
```

- [ ] **Step 3: Metodologia, linha 143**

Antes:
```
\item \textbf{DSUTarjan}: Combina \texttt{rank[]} com compressão de caminho no \texttt{find}, implementado recursivamente --- cada chamada faz o nó visitado apontar diretamente para a raiz retornada.
```

Depois:
```
\item \textbf{DSUTarjan}: Combina \texttt{rank[]} com compressão de caminho no \texttt{find}. A implementação é recursiva: cada chamada faz o nó visitado apontar diretamente para a raiz retornada.
```

- [ ] **Step 4: Resultados, linha 201 (duas ocorrências)**

Antes:
```
...o número de acessos cresce por um fator de aproximadamente 101, o que é compatível com comportamento quadrático total --- custo $O(n)$ por operação acumulado sobre $m = 3n$ operações resulta em $O(n^2)$ acessos. Union by Rank e Tarjan, em contraste, crescem de forma muito mais contida, com comportamento aproximadamente linear em $n$ --- reflexo de operações com custo sublinear acumuladas sobre $\Theta(m) = \Theta(n)$ operações.
```

Depois:
```
...o número de acessos cresce por um fator de aproximadamente 101. Esse valor é compatível com comportamento quadrático total: custo $O(n)$ por operação acumulado sobre $m = 3n$ operações resulta em $O(n^2)$ acessos. Union by Rank e Tarjan, em contraste, crescem de forma muito mais contida, com comportamento aproximadamente linear em $n$. É o reflexo natural de operações com custo sublinear acumuladas sobre $\Theta(m) = \Theta(n)$ chamadas.
```

- [ ] **Step 5: Resultados, linha 300 (descrição do custo por aresta)**

Antes:
```
A Figura~\ref{fig:custo_aresta} mostra o custo médio por aresta. Para o Naive, esse custo cresce linearmente com $n$ (reflexo do comportamento $O(n)$ por operação). Para Rank, o crescimento é lento e logarítmico. Para Tarjan, o custo permanece praticamente constante --- essa estabilidade é a manifestação empírica mais direta da complexidade amortizada $O(\alpha(n))$.
```

Depois (quebra o paralelismo "Para X / Para Y / Para Z"):
```
A Figura~\ref{fig:custo_aresta} mostra o custo médio por aresta. No Naive, esse custo cresce linearmente com $n$, como seria de se esperar de operações $O(n)$. Rank apresenta crescimento lento, compatível com uma dependência logarítmica. Tarjan é o caso mais interessante: o custo por aresta fica praticamente plano ao longo de duas ordens de grandeza, e é essa estabilidade que corresponde, empiricamente, à complexidade amortizada $O(\alpha(n))$.
```

- [ ] **Step 6: Conclusão, linhas 313-315 (quebrar anáfora "Quanto à...")**

Antes:
```
Os experimentos respondem às duas perguntas propostas na introdução. Quanto à primeira --- em que escala a implementação ingênua deixa de ser viável ---, os resultados são bastante claros: já em $n = 10.000$, a versão Naive gasta quase 57 ms enquanto Rank e Tarjan ficam abaixo de 6 ms; em $n = 100.000$, a diferença passa de 160 vezes no tempo e de 3.750 vezes no número de acessos. Para qualquer aplicação de MST em escala real, o Naive é simplesmente inviável.

Quanto à segunda questão --- o ganho marginal de acrescentar Path Compression ao Union by Rank ---, o resultado é positivo mas mais modesto: a compressão reduz os acessos em cerca de 33\% e o tempo em torno de 6\% em $n = 10^5$. Isso é suficiente para manter Tarjan consistentemente mais rápido que Rank, mas a grande conquista já havia sido feita pelo Union by Rank. O resultado prático mais importante é que, com a estrutura de Tarjan, o custo do DSU deixa de dominar o algoritmo de Kruskal, cujo gargalo passa a ser a ordenação das arestas em $O(m \log m)$.
```

Depois (molde estrutural; **os valores numéricos concretos devem vir do `benchmark_detalhado.csv` atualizado**):
```
A primeira pergunta da introdução era em que escala a implementação ingênua deixa de ser viável. Os experimentos respondem de forma direta: já em $n = 10.000$, a versão Naive gasta perto de {TEMPO_NAIVE_10K} ms, enquanto Rank e Tarjan ficam abaixo de {TEMPO_OTIM_10K} ms. Em $n = 10^5$, a diferença passa de {FATOR_TEMPO} vezes no tempo e de {FATOR_OPS} vezes no número de acessos. Para qualquer aplicação de MST em escala real, Naive é simplesmente inviável.

A segunda pergunta mirava o ganho marginal de acrescentar Path Compression ao Union by Rank. O resultado é positivo, mas mais modesto do que o passo anterior: a compressão reduz os acessos em cerca de {REDUCAO_OPS}\% e o tempo em torno de {REDUCAO_TEMPO}\% em $n = 10^5$. É suficiente para manter Tarjan consistentemente à frente de Rank, mas a conquista principal já havia sido feita pelo Union by Rank. Na prática, o que importa mais é que, com a estrutura completa de Tarjan, o custo do DSU deixa de dominar o Kruskal: o gargalo passa a ser a ordenação das arestas em $O(m \log m)$.
```

**IMPORTANTE:** substituir os marcadores `{TEMPO_NAIVE_10K}`, `{TEMPO_OTIM_10K}`, `{FATOR_TEMPO}`, `{FATOR_OPS}`, `{REDUCAO_OPS}` e `{REDUCAO_TEMPO}` pelos valores reais calculados a partir do CSV. Fórmulas:
- `FATOR_TEMPO` = `naive_tempo_medio / max(rank_tempo_medio, tarjan_tempo_medio)` em n=100000.
- `FATOR_OPS` = `naive_ops_media / tarjan_ops_media` em n=100000.
- `REDUCAO_OPS` = `(rank_ops_media - tarjan_ops_media) / rank_ops_media * 100` em n=100000.
- `REDUCAO_TEMPO` = `(rank_tempo_medio - tarjan_tempo_medio) / rank_tempo_medio * 100` em n=100000.
- `TEMPO_NAIVE_10K` e `TEMPO_OTIM_10K` = média de Rank e Tarjan em n=10000, com duas casas decimais.

Na prática, abrir o CSV, ler os números, escrever direto no .tex. Nenhum `{...}` pode sobreviver no commit.

- [ ] **Step 7: Verificar contagem final de travessões**

Run (Grep no arquivo artigo.tex): contar ocorrências do padrão `---`.
Expected: ≤ 3 ocorrências. Se passar de 3, escolher a mais dispensável e substituir por vírgula, dois-pontos ou parêntese.

- [ ] **Step 8: Commit**

```bash
git add artigo.tex
git commit -m "docs(artigo): reduz travessoes e quebra paralelismos rigidos no texto"
```

---

## Task 7: Humanização — ritmo e frases-modelo

**Files:**
- Modify: `artigo.tex` (passagens específicas)

Contexto: passada dedicada a tirar frases que soam robóticas (abertura padronizada, transições formulaicas) e variar comprimento de frase.

- [ ] **Step 1: Introdução, linha 37 (abertura)**

Antes:
```
O problema de gerenciar conjuntos disjuntos aparece em diversos contextos da computação. Seja no processamento de imagens, onde pixels vizinhos precisam ser agrupados em regiões, ou em redes de computadores, onde é preciso verificar se dois nós pertencem à mesma componente conectada, a necessidade de manter e consultar partições de elementos é recorrente.
```

Depois:
```
Gerenciar conjuntos disjuntos é um problema recorrente. Aparece no processamento de imagens, quando pixels vizinhos precisam ser agrupados em regiões. Aparece em redes de computadores, quando se quer verificar se dois nós pertencem à mesma componente. E aparece, talvez da forma mais explícita, em algoritmos de grafos que precisam consultar conectividade a todo momento.
```

- [ ] **Step 2: Resultados, linha 201 (abertura do comentário)**

Antes:
```
Os dados revelam um comportamento consistente com a teoria.
```

Depois (remover a frase-modelo e começar direto pela análise):
```
A versão ingênua apresenta crescimento claramente superlinear:
```

(Ajustar o período para começar direto. O restante do parágrafo emenda naturalmente.)

- [ ] **Step 3: Resultados, linha 201 (final)**

Antes:
```
...reflexo de operações com custo sublinear acumuladas sobre $\Theta(m) = \Theta(n)$ chamadas. A análise quantitativa dessa diferença, por meio do expoente empírico de crescimento, é retomada adiante, na discussão de classes de complexidade.
```

Depois:
```
...reflexo de operações com custo sublinear acumuladas sobre $\Theta(m) = \Theta(n)$ chamadas. O expoente empírico de crescimento, mostrado mais adiante, torna essa diferença quantitativa.
```

- [ ] **Step 4: Metodologia, linha 132 (frase-modelo)**

Antes:
```
Essa organização facilita a extensibilidade: para testar uma nova variante de DSU, basta criar uma classe que implemente a interface e passá-la ao Kruskal, sem alterar nenhum outro código. Além disso, a separação entre a lógica do grafo e a estrutura de conjuntos disjuntos permite que cada módulo seja testado de forma independente.
```

Depois:
```
A vantagem dessa divisão é prática: uma nova variante de DSU entra no experimento criando-se uma classe que implemente a interface e passando-a ao Kruskal. Nenhum outro arquivo precisa mudar. Como consequência secundária, grafo e DSU podem ser testados em isolamento.
```

- [ ] **Step 5: Resultados, linha 242 (tempos de execução)**

Antes:
```
Os tempos de execução, medidos após fase de \textit{warm-up} do JIT, seguem a mesma hierarquia das operações de acesso. A versão Naive atinge quase 14,5 segundos em $n = 10^5$, enquanto Rank e Tarjan ficam abaixo de 90 ms --- uma diferença superior a 160 vezes. Tarjan é mais rápido que Rank em todos os pontos, exceto em $n = 10.000$ (5,98 ms vs.\ 5,94 ms), diferença dentro da margem de variação de medição única.
```

Depois (substituir `{...}` pelos valores concretos do CSV; travessão removido):
```
Os tempos de execução, medidos após a fase de \textit{warm-up} do JIT, seguem a mesma hierarquia das contagens de acesso. Em $n = 10^5$, a versão Naive chega a quase {TEMPO_NAIVE_100K} ms, enquanto Rank e Tarjan ficam abaixo de {TEMPO_OTIM_100K} ms, uma diferença superior a {FATOR} vezes. Tarjan é mais rápido que Rank em praticamente todos os pontos; eventuais inversões ficam dentro da margem do desvio padrão amostral.
```

**Valores:** ler do CSV em n=100000. `{TEMPO_NAIVE_100K}` e `{TEMPO_OTIM_100K}` com duas casas; `{FATOR}` = razão Naive/max(Rank,Tarjan) arredondada para inteiro.

- [ ] **Step 6: Fundamentação, linha 90 (estilo)**

Antes:
```
A sua inversa, $\alpha(n) = \min\{k \geq 1 : A(k, 1) \geq n\}$, cresce de forma correspondentemente lenta: para qualquer valor de $n$ concebível no universo físico, $\alpha(n) \leq 4$ \cite{cormen2009}. Embora a complexidade não seja formalmente $O(1)$, é \textit{efetivamente constante} para todos os fins práticos.
```

Depois:
```
Sua inversa $\alpha(n) = \min\{k \geq 1 : A(k, 1) \geq n\}$ cresce com a mesma lentidão invertida. Para qualquer $n$ imaginável no universo físico, $\alpha(n) \leq 4$ \cite{cormen2009}. Formalmente a complexidade não é $O(1)$; na prática, é constante.
```

- [ ] **Step 7: Revisão geral de ritmo**

Ler o artigo inteiro uma vez e procurar:
- Parágrafos com 4+ frases no mesmo comprimento (~20 palavras cada). Misturar uma curta.
- Parágrafos que abrem com "Em primeiro lugar", "É importante", "Vale ressaltar", "Além disso". Se encontrar, reescrever.
- Listas com bullets perfeitamente paralelos onde prosa corrida funcionaria melhor.

Aplicar ajustes pontuais conforme encontrar.

- [ ] **Step 8: Commit**

```bash
git add artigo.tex
git commit -m "docs(artigo): ritmo de frase e remocao de aberturas padronizadas"
```

---

## Task 8: Validação final

**Files:**
- Read: `artigo.tex` completo
- Optional: compilar LaTeX

- [ ] **Step 1: Compilar LaTeX (se disponível)**

Run: `pdflatex -interaction=nonstopmode artigo.tex`
Expected: PDF gerado sem erros. Se pdflatex não estiver instalado, pular.

- [ ] **Step 2: Contagem final de travessões**

Confirmar que `---` no artigo.tex aparece no máximo 3 vezes.

- [ ] **Step 3: Grep por frases-modelo residuais**

Verificar que nenhuma das seguintes strings aparece no artigo:
- "Os dados revelam"
- "Em primeiro lugar"
- "É importante destacar"
- "Vale ressaltar"
- "Cabe salientar"
- "Além disso" no início de parágrafo (meio de parágrafo tudo bem)

- [ ] **Step 4: Rodar `exemploKruskal()` uma última vez**

Run: `echo -e "1\n0" | java -cp out Main`
Expected: três blocos com o mesmo peso, linha final "Validacao OK".

- [ ] **Step 5: Verificar benchmark_detalhado.csv não foi perdido**

Run: `ls -la benchmark_detalhado.csv`

- [ ] **Step 6: git status limpo**

Run: `git status`
Expected: "nothing to commit, working tree clean".

---

## Resumo das tarefas

1. **Main.java** — validação automática em `exemploKruskal()`.
2. **Benchmark.java** — arrays + média + desvio padrão + saída CSV.
3. **gerar_graficos.py** — leitura de CSV + errorbars.
4. **Tabelas do artigo** — valores novos, tempo com ± σ.
5. **Metodologia e Limitações** — refletir setup de 5 seeds.
6. **Humanização parte 1** — travessões e paralelismos.
7. **Humanização parte 2** — ritmo e frases-modelo.
8. **Validação final** — compilação, checagens, git clean.
