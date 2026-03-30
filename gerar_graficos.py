import matplotlib.pyplot as plt
import numpy as np

# dados coletados do benchmark (media de execucoes)
# n, m, variante, tempo_ms, operacoes
dados_brutos = """
1000,3000,Naive,3.40,647373
1000,3000,Union by Rank,1.82,27377
1000,3000,Tarjan (Rank + Compressao),1.50,19897
5000,15000,Naive,18.11,21438927
5000,15000,Union by Rank,6.62,157485
5000,15000,Tarjan (Rank + Compressao),5.26,108137
10000,30000,Naive,54.97,80819671
10000,30000,Union by Rank,6.13,324011
10000,30000,Tarjan (Rank + Compressao),6.60,216791
50000,150000,Naive,3956.49,2029460441
50000,150000,Union by Rank,65.14,1674589
50000,150000,Tarjan (Rank + Compressao),54.59,1084927
100000,300000,Naive,46038.13,8148600321
100000,300000,Union by Rank,134.96,3257913
100000,300000,Tarjan (Rank + Compressao),182.07,2155953
"""

# parseia os dados
naive_n, naive_tempo, naive_ops = [], [], []
rank_n, rank_tempo, rank_ops = [], [], []
tarjan_n, tarjan_tempo, tarjan_ops = [], [], []

for linha in dados_brutos.strip().split("\n"):
    partes = linha.split(",")
    n = int(partes[0])
    tempo = float(partes[3])
    ops = int(partes[4])
    variante = partes[2]

    if variante == "Naive":
        naive_n.append(n)
        naive_tempo.append(tempo)
        naive_ops.append(ops)
    elif variante == "Union by Rank":
        rank_n.append(n)
        rank_tempo.append(tempo)
        rank_ops.append(ops)
    else:
        tarjan_n.append(n)
        tarjan_tempo.append(tempo)
        tarjan_ops.append(ops)

# configuracao geral dos graficos
plt.rcParams['figure.figsize'] = (10, 6)
plt.rcParams['font.size'] = 11

# ============================================
# GRAFICO 1 - Operacoes de acesso (todas)
# ============================================
fig, ax = plt.subplots()
ax.plot(naive_n, naive_ops, 'ro-', label='Naive', linewidth=2, markersize=7)
ax.plot(rank_n, rank_ops, 'bs-', label='Union by Rank', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_ops, 'g^-', label='Tarjan (Rank + Compressao)', linewidth=2, markersize=7)

ax.set_xlabel('Numero de vertices (n)')
ax.set_ylabel('Operacoes de acesso')
ax.set_title('Comparacao de Operacoes - DSU no Algoritmo de Kruskal')
ax.legend()
ax.grid(True, alpha=0.3)
ax.ticklabel_format(style='scientific', axis='y', scilimits=(0,0))

plt.tight_layout()
plt.savefig('grafico_operacoes_todas.png', dpi=150)
plt.close()
print("Salvo: grafico_operacoes_todas.png")

# ============================================
# GRAFICO 2 - Operacoes (so Rank e Tarjan, pra ver a diferenca)
# ============================================
fig, ax = plt.subplots()
ax.plot(rank_n, rank_ops, 'bs-', label='Union by Rank', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_ops, 'g^-', label='Tarjan (Rank + Compressao)', linewidth=2, markersize=7)

ax.set_xlabel('Numero de vertices (n)')
ax.set_ylabel('Operacoes de acesso')
ax.set_title('Comparacao de Operacoes - Rank vs Tarjan')
ax.legend()
ax.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig('grafico_operacoes_rank_tarjan.png', dpi=150)
plt.close()
print("Salvo: grafico_operacoes_rank_tarjan.png")

# ============================================
# GRAFICO 3 - Tempo de execucao (todas)
# ============================================
fig, ax = plt.subplots()
ax.plot(naive_n, naive_tempo, 'ro-', label='Naive', linewidth=2, markersize=7)
ax.plot(rank_n, rank_tempo, 'bs-', label='Union by Rank', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_tempo, 'g^-', label='Tarjan (Rank + Compressao)', linewidth=2, markersize=7)

ax.set_xlabel('Numero de vertices (n)')
ax.set_ylabel('Tempo (ms)')
ax.set_title('Comparacao de Tempo de Execucao - DSU no Algoritmo de Kruskal')
ax.legend()
ax.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig('grafico_tempo_todas.png', dpi=150)
plt.close()
print("Salvo: grafico_tempo_todas.png")

# ============================================
# GRAFICO 4 - Tempo (so Rank e Tarjan)
# ============================================
fig, ax = plt.subplots()
ax.plot(rank_n, rank_tempo, 'bs-', label='Union by Rank', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_tempo, 'g^-', label='Tarjan (Rank + Compressao)', linewidth=2, markersize=7)

ax.set_xlabel('Numero de vertices (n)')
ax.set_ylabel('Tempo (ms)')
ax.set_title('Comparacao de Tempo - Rank vs Tarjan')
ax.legend()
ax.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig('grafico_tempo_rank_tarjan.png', dpi=150)
plt.close()
print("Salvo: grafico_tempo_rank_tarjan.png")

# ============================================
# GRAFICO 5 - Escala logaritmica (operacoes) - mostra as classes de complexidade
# ============================================
fig, ax = plt.subplots()
ax.plot(naive_n, naive_ops, 'ro-', label='Naive - O(n)', linewidth=2, markersize=7)
ax.plot(rank_n, rank_ops, 'bs-', label='Union by Rank - O(log n)', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_ops, 'g^-', label='Tarjan - O(α(n))', linewidth=2, markersize=7)

ax.set_xlabel('Numero de vertices (n)')
ax.set_ylabel('Operacoes de acesso (escala log)')
ax.set_title('Comparacao em Escala Logaritmica - Classes de Complexidade')
ax.set_yscale('log')
ax.legend()
ax.grid(True, alpha=0.3, which='both')

plt.tight_layout()
plt.savefig('grafico_log_operacoes.png', dpi=150)
plt.close()
print("Salvo: grafico_log_operacoes.png")

# ============================================
# GRAFICO 6 - Operacoes por operacao (media por find/union)
# ============================================
# como m = 3n, e kruskal faz ~2 finds + 1 union por aresta no pior caso
# vamos calcular ops/m pra ter a media por operacao
fig, ax = plt.subplots()
ms = [3*n for n in naive_n]

naive_ops_por_m = [o/m for o, m in zip(naive_ops, ms)]
rank_ops_por_m = [o/m for o, m in zip(rank_ops, ms)]
tarjan_ops_por_m = [o/m for o, m in zip(tarjan_ops, ms)]

ax.plot(naive_n, naive_ops_por_m, 'ro-', label='Naive', linewidth=2, markersize=7)
ax.plot(rank_n, rank_ops_por_m, 'bs-', label='Union by Rank', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_ops_por_m, 'g^-', label='Tarjan (Rank + Compressao)', linewidth=2, markersize=7)

ax.set_xlabel('Numero de vertices (n)')
ax.set_ylabel('Operacoes medias por aresta')
ax.set_title('Custo Medio por Aresta - Evidencia de Complexidade Amortizada')
ax.legend()
ax.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig('grafico_custo_por_aresta.png', dpi=150)
plt.close()
print("Salvo: grafico_custo_por_aresta.png")

print("\nTodos os graficos foram gerados com sucesso!")
