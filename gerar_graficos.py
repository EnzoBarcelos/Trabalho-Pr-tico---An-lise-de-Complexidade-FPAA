import matplotlib.pyplot as plt
import numpy as np

# dados coletados do benchmark com warm-up JIT (seed=42, unica execucao)
# n, m, variante, tempo_ms, operacoes
dados_brutos = """
1000,3000,Naive,1.49,647373
1000,3000,Union by Rank,1.23,27377
1000,3000,Tarjan (Rank + Compressao),0.76,19897
5000,15000,Naive,16.83,21438927
5000,15000,Union by Rank,2.93,157485
5000,15000,Tarjan (Rank + Compressao),2.67,108137
10000,30000,Naive,56.58,80819671
10000,30000,Union by Rank,5.94,324011
10000,30000,Tarjan (Rank + Compressao),5.98,216791
50000,150000,Naive,3320.02,2029460441
50000,150000,Union by Rank,43.09,1674589
50000,150000,Tarjan (Rank + Compressao),39.00,1084927
100000,300000,Naive,14519.67,8148600321
100000,300000,Union by Rank,89.33,3257913
100000,300000,Tarjan (Rank + Compressao),84.27,2155953
"""

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

plt.rcParams['figure.figsize'] = (10, 6)
plt.rcParams['font.size'] = 11

# ============================================
# GRAFICO 1 - Operacoes de acesso (todas)
# ============================================
fig, ax = plt.subplots()
ax.plot(naive_n, naive_ops, 'ro-', label='Naive', linewidth=2, markersize=7)
ax.plot(rank_n, rank_ops, 'bs-', label='Union by Rank', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_ops, 'g^-', label='Tarjan (Rank + Compressão)', linewidth=2, markersize=7)

ax.set_xlabel('Número de vértices (n)')
ax.set_ylabel('Acessos ao array pai[]')
ax.set_title('Comparação de Operações de Acesso — DSU no Algoritmo de Kruskal')
ax.legend()
ax.grid(True, alpha=0.3)
ax.ticklabel_format(style='scientific', axis='y', scilimits=(0,0))

plt.tight_layout()
plt.savefig('grafico_operacoes_todas.png', dpi=150)
plt.close()
print("Salvo: grafico_operacoes_todas.png")

# ============================================
# GRAFICO 2 - Operacoes (so Rank e Tarjan)
# ============================================
fig, ax = plt.subplots()
ax.plot(rank_n, rank_ops, 'bs-', label='Union by Rank', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_ops, 'g^-', label='Tarjan (Rank + Compressão)', linewidth=2, markersize=7)

ax.set_xlabel('Número de vértices (n)')
ax.set_ylabel('Acessos ao array pai[]')
ax.set_title('Comparação de Operações — Union by Rank vs Tarjan')
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
ax.plot(tarjan_n, tarjan_tempo, 'g^-', label='Tarjan (Rank + Compressão)', linewidth=2, markersize=7)

ax.set_xlabel('Número de vértices (n)')
ax.set_ylabel('Tempo de execução (ms)')
ax.set_title('Comparação de Tempo de Execução — DSU no Algoritmo de Kruskal')
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
ax.plot(tarjan_n, tarjan_tempo, 'g^-', label='Tarjan (Rank + Compressão)', linewidth=2, markersize=7)

ax.set_xlabel('Número de vértices (n)')
ax.set_ylabel('Tempo de execução (ms)')
ax.set_title('Comparação de Tempo — Union by Rank vs Tarjan')
ax.legend()
ax.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig('grafico_tempo_rank_tarjan.png', dpi=150)
plt.close()
print("Salvo: grafico_tempo_rank_tarjan.png")

# ============================================
# GRAFICO 5 - Escala logaritmica (operacoes)
# ============================================
fig, ax = plt.subplots()
ax.plot(naive_n, naive_ops, 'ro-', label='Naive — O(n)', linewidth=2, markersize=7)
ax.plot(rank_n, rank_ops, 'bs-', label='Union by Rank — O(log n)', linewidth=2, markersize=7)
ax.plot(tarjan_n, tarjan_ops, 'g^-', label='Tarjan — O(α(n))', linewidth=2, markersize=7)

ax.set_xlabel('Número de vértices (n)')
ax.set_ylabel('Acessos ao array pai[] (escala log)')
ax.set_title('Classes de Complexidade em Escala Logarítmica')
ax.set_yscale('log')
ax.legend()
ax.grid(True, alpha=0.3, which='both')

plt.tight_layout()
plt.savefig('grafico_log_operacoes.png', dpi=150)
plt.close()
print("Salvo: grafico_log_operacoes.png")

# ============================================
# GRAFICO 6 - Custo medio por aresta
# ============================================
fig, ax = plt.subplots()
ms = [3*n for n in naive_n]

naive_ops_por_m  = [o/m for o, m in zip(naive_ops,  ms)]
rank_ops_por_m   = [o/m for o, m in zip(rank_ops,   ms)]
tarjan_ops_por_m = [o/m for o, m in zip(tarjan_ops, ms)]

ax.plot(naive_n, naive_ops_por_m,  'ro-', label='Naive',                    linewidth=2, markersize=7)
ax.plot(rank_n,  rank_ops_por_m,   'bs-', label='Union by Rank',            linewidth=2, markersize=7)
ax.plot(tarjan_n,tarjan_ops_por_m, 'g^-', label='Tarjan (Rank + Compressão)', linewidth=2, markersize=7)

ax.set_xlabel('Número de vértices (n)')
ax.set_ylabel('Acessos médios por aresta')
ax.set_title('Custo Médio por Aresta — Evidência de Complexidade Amortizada')
ax.legend()
ax.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig('grafico_custo_por_aresta.png', dpi=150)
plt.close()
print("Salvo: grafico_custo_por_aresta.png")

# ============================================
# GRAFICO 7 - Expoente empirico entre pares consecutivos de n
# ============================================
# expoente p = log(ops_i+1 / ops_i) / log(n_i+1 / n_i)
# se ops crece como n^p, entao p aproxima o expoente real da classe de complexidade

rotulos = ['1k→5k', '5k→10k', '10k→50k', '50k→100k']
x = np.arange(len(rotulos))

def calcular_expoentes(ns, ops):
    exps = []
    for i in range(len(ns) - 1):
        if ops[i] > 0 and ns[i] > 0:
            exps.append(np.log(ops[i+1] / ops[i]) / np.log(ns[i+1] / ns[i]))
    return exps

naive_exp  = calcular_expoentes(naive_n,  naive_ops)
rank_exp   = calcular_expoentes(rank_n,   rank_ops)
tarjan_exp = calcular_expoentes(tarjan_n, tarjan_ops)

largura = 0.25
fig, ax = plt.subplots()
ax.bar(x - largura, naive_exp,  largura, label='Naive',                     color='#e74c3c', alpha=0.85)
ax.bar(x,           rank_exp,   largura, label='Union by Rank',              color='#3498db', alpha=0.85)
ax.bar(x + largura, tarjan_exp, largura, label='Tarjan (Rank + Compressão)', color='#2ecc71', alpha=0.85)

# linha de referencia para os expoentes teoricos
ax.axhline(y=2.0, color='#e74c3c', linestyle='--', alpha=0.5, linewidth=1, label='Expoente teórico Naive = 2')
ax.axhline(y=1.0, color='#3498db', linestyle='--', alpha=0.5, linewidth=1, label='Expoente teórico Rank/Tarjan ≈ 1')

ax.set_xlabel('Transição entre valores de n')
ax.set_ylabel('Expoente empírico p')
ax.set_title('Expoente Empírico de Crescimento das Operações')
ax.set_xticks(x)
ax.set_xticklabels(rotulos)
ax.legend(fontsize=9)
ax.grid(True, alpha=0.3, axis='y')
ax.set_ylim(0, 2.6)

plt.tight_layout()
plt.savefig('grafico_expoente_empirico.png', dpi=150)
plt.close()
print("Salvo: grafico_expoente_empirico.png")

print("\nTodos os gráficos foram gerados com sucesso!")
