# Melhorias no Trabalho Prático FPAA — Tier B

Data: 2026-04-23
Autor: Enzo Barcelos
Escopo: polimento + novos experimentos com variância

## Objetivo

Elevar o trabalho existente em três frentes: validação automatizada, rigor estatístico (múltiplas seeds + variância) e humanização do texto do artigo. Sem adicionar novas variantes de DSU e sem alterar a estrutura conceitual do trabalho.

## Motivação

Três problemas concretos identificados na revisão:

1. O artigo afirma validar corretude comparando pesos de MST entre as três variantes, mas `Main.exemploKruskal()` só imprime os valores — não existe assert. A alegação do artigo precede o comportamento do código.
2. A seção "Limitações" registra que "variância amostral não foi estimada", mas `Benchmark.executarDetalhado()` já roda com 5 seeds distintas. A limitação é artificial — basta usar o que já existe e agregar desvio padrão.
3. O texto do artigo contém marcadores típicos de geração por IA: excesso de travessões (`---`), construções paralelas rígidas ("Quanto à primeira... Quanto à segunda"), frases-modelo tipo "Os dados revelam comportamento consistente com a teoria". A memória do usuário (PUC Minas) flaga isso como risco de anulação.

## Mudanças propostas

### 1. Código Java

**`src/Main.java`**

Em `exemploKruskal()`, guardar o peso da primeira variante como referência e comparar as demais com tolerância `1e-9`. Imprimir "Validação: OK" se iguais, lançar `AssertionError` caso contrário. A inspeção visual vira verificação programática.

**`src/benchmark/Benchmark.java`**

Em `executarDetalhado()`:
- Substituir os acumuladores `double somaTempos` e `long somaOps` por arrays `double[]` de tamanho igual ao número de seeds.
- Calcular média e desvio padrão amostral (denominador `n-1`).
- Manter a saída em tabela legível no stdout (já existente).
- Adicionar escrita num arquivo CSV `benchmark_detalhado.csv` na raiz do projeto, com cabeçalho:
  `n,m,variante,tempo_medio,tempo_std,ops_media,ops_std`
- Valores de ponto flutuante com `Locale.US` (ponto decimal) para o Python parsear direto.

### 2. Script Python

**`gerar_graficos.py`**

- Receber caminho do CSV via `sys.argv[1]` com default `benchmark_detalhado.csv`.
- Ler com `csv.DictReader` (fica mais legível que `split`).
- Manter os 7 gráficos com aparência idêntica; usar as médias como valor central.
- Adicionar `ax.errorbar(..., yerr=stds)` nos gráficos de tempo (`grafico_tempo_todas.png`, `grafico_tempo_rank_tarjan.png`). Operações são determinísticas a menos do grafo — σ nos contadores é pequena, não vale poluir o gráfico com barras minúsculas.

### 3. Execução

Na raiz do projeto:
```bash
javac -d out src/*.java src/**/*.java
echo 3 | java -cp out Main          # opção 3 do menu
python gerar_graficos.py benchmark_detalhado.csv
```

Verificar que 7 PNGs foram regerados.

### 4. Artigo (`artigo.tex`)

**Metodologia:**
- Atualizar a subseção "Cenários de Teste" ou "Ambiente de Execução" explicando que cada ponto das tabelas é média sobre 5 seeds (42, 43, 44, 45, 46), com desvio padrão amostral reportado para tempos.

**Tabelas:**
- Tabela de tempo vira `média ± σ` (usa `$\pm$` em LaTeX).
- Tabela de operações continua valor único (média sobre as 5 seeds).

**Resultados:**
- Atualizar comentários textuais com os novos números (se diferirem dos atuais de forma relevante).
- Recalcular expoentes empíricos com as novas médias.

**Limitações:**
- Remover a frase "Os tempos medidos correspondem a execuções únicas com semente fixa; variância amostral não foi estimada".
- Manter apenas a limitação sobre topologia enviesada (essa continua real).

**Humanização (passo em separado):**
- Reduzir `---` no corpo do artigo de ~10 para no máximo 3.
- Quebrar os paralelismos mais óbvios:
  - Conclusão: "Quanto à primeira... Quanto à segunda" → prosa contínua.
  - Resultados: "Para o Naive... Para Rank... Para Tarjan" → frase integradora + detalhes caso a caso.
  - Introdução: "Seja no processamento de imagens... ou em redes" → reescrita com exemplos mais específicos.
- Remover frases-modelo que soam geradas: "Os dados revelam um comportamento consistente com a teoria", "Essa organização facilita a extensibilidade", "A análise quantitativa dessa diferença, por meio do expoente empírico de crescimento, é retomada adiante".
- Variar comprimento de frase: intercalar curtas (~10 palavras) com longas (~30+). Hoje o texto tem ritmo muito uniforme.
- Permitir construções humanas: conjunção inicial ocasional ("Mas", "E"), parênteses explicativos, alguma repetição natural.

**Não tocar:**
- Equações e derivações matemáticas (função de Ackermann, análise do potencial).
- Figuras, gráficos, legendas.
- Estrutura de seções e subseções.
- Referências bibliográficas.
- Conteúdo técnico em si — só ritmo, estrutura sintática e escolha de palavras.

## Ordem de execução

1. Java: Main + Benchmark (isolado, compila e testa).
2. Rodar benchmark detalhado, gerar CSV.
3. Python: atualizar script para ler CSV, rerodar, verificar PNGs.
4. Artigo: tabelas com novos números + ajustes em Metodologia e Limitações.
5. Humanização: passada dedicada só em texto, commit separado para facilitar revisão.
6. Validação final: compilar LaTeX (se possível) ou pelo menos ler o `.tex` inteiro procurando inconsistências.

## Riscos

- **Compilação Java**: Java 24 disponível, sem blockers aparentes.
- **Variância de tempo**: como as seeds geram grafos diferentes, o σ pode ficar alto (grafos distintos têm tamanhos ligeiramente diferentes por causa do descarte de self-loops). Isso é esperado e vai aparecer no artigo.
- **Contagem de operações**: como cada seed gera grafo diferente, a contagem de operações também varia (não é mais determinística como era com seed única). Isso precisa ser explicado na Metodologia.
- **Humanização subjetiva**: o resultado depende de julgamento. Mitigação: commit separado, fácil reverter trecho específico.

## Critério de conclusão

- `exemploKruskal()` falha se qualquer variante divergir.
- `benchmark_detalhado.csv` gerado com 15 linhas (3 variantes × 5 tamanhos).
- 7 PNGs regerados sem erro.
- Tabelas do artigo com novos valores.
- Seção Limitações sem a frase sobre variância.
- Contagem de travessões `---` no `.tex` ≤ 3.
- Nenhuma das frases-modelo listadas acima sobrevive no texto final.
