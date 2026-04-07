from collections import defaultdict

import matplotlib.pyplot as plt


dados = [
    (1000, 3000, "Naive", 3.40, 647373),
    (1000, 3000, "Union by Rank", 1.82, 27377),
    (1000, 3000, "Tarjan (Rank + Compressao)", 1.50, 19897),
    (5000, 15000, "Naive", 18.11, 21438927),
    (5000, 15000, "Union by Rank", 6.62, 157485),
    (5000, 15000, "Tarjan (Rank + Compressao)", 5.26, 108137),
    (10000, 30000, "Naive", 54.97, 80819671),
    (10000, 30000, "Union by Rank", 6.13, 324011),
    (10000, 30000, "Tarjan (Rank + Compressao)", 6.60, 216791),
    (50000, 150000, "Naive", 3956.49, 2029460441),
    (50000, 150000, "Union by Rank", 65.14, 1674589),
    (50000, 150000, "Tarjan (Rank + Compressao)", 54.59, 1084927),
    (100000, 300000, "Naive", 46038.13, 8148600321),
    (100000, 300000, "Union by Rank", 134.96, 3257913),
    (100000, 300000, "Tarjan (Rank + Compressao)", 182.07, 2155953),
]


def separar_por_variante(registros):
    grupos = defaultdict(lambda: {"n": [], "m": [], "tempo": [], "ops": []})
    for n, m, variante, tempo, ops in registros:
        grupos[variante]["n"].append(n)
        grupos[variante]["m"].append(m)
        grupos[variante]["tempo"].append(tempo)
        grupos[variante]["ops"].append(ops)
    return grupos


def salvar_grafico(nome, titulo, desenhar):
    fig, ax = plt.subplots()
    desenhar(ax)
    ax.set_title(titulo)
    ax.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(nome, dpi=150)
    plt.close(fig)
    print(f"Salvo: {nome}")


def main():
    grupos = separar_por_variante(dados)
    naive = grupos["Naive"]
    rank = grupos["Union by Rank"]
    tarjan = grupos["Tarjan (Rank + Compressao)"]

    plt.rcParams["figure.figsize"] = (10, 6)
    plt.rcParams["font.size"] = 11

    salvar_grafico(
        "grafico_operacoes_todas.png",
        "Comparacao de Operacoes - DSU no Kruskal",
        lambda ax: (
            ax.plot(naive["n"], naive["ops"], "ro-", label="Naive", linewidth=2, markersize=7),
            ax.plot(rank["n"], rank["ops"], "bs-", label="Union by Rank", linewidth=2, markersize=7),
            ax.plot(tarjan["n"], tarjan["ops"], "g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Operacoes de acesso"),
            ax.legend(),
            ax.ticklabel_format(style="scientific", axis="y", scilimits=(0, 0)),
        ),
    )

    salvar_grafico(
        "grafico_operacoes_rank_tarjan.png",
        "Comparacao de Operacoes - Rank vs Tarjan",
        lambda ax: (
            ax.plot(rank["n"], rank["ops"], "bs-", label="Union by Rank", linewidth=2, markersize=7),
            ax.plot(tarjan["n"], tarjan["ops"], "g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Operacoes de acesso"),
            ax.legend(),
        ),
    )

    salvar_grafico(
        "grafico_tempo_todas.png",
        "Comparacao de Tempo de Execucao - DSU no Kruskal",
        lambda ax: (
            ax.plot(naive["n"], naive["tempo"], "ro-", label="Naive", linewidth=2, markersize=7),
            ax.plot(rank["n"], rank["tempo"], "bs-", label="Union by Rank", linewidth=2, markersize=7),
            ax.plot(tarjan["n"], tarjan["tempo"], "g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Tempo (ms)"),
            ax.legend(),
        ),
    )

    salvar_grafico(
        "grafico_tempo_rank_tarjan.png",
        "Comparacao de Tempo - Rank vs Tarjan",
        lambda ax: (
            ax.plot(rank["n"], rank["tempo"], "bs-", label="Union by Rank", linewidth=2, markersize=7),
            ax.plot(tarjan["n"], tarjan["tempo"], "g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Tempo (ms)"),
            ax.legend(),
        ),
    )

    salvar_grafico(
        "grafico_log_operacoes.png",
        "Comparacao em Escala Logaritmica - Classes de Complexidade",
        lambda ax: (
            ax.plot(naive["n"], naive["ops"], "ro-", label="Naive - O(n)", linewidth=2, markersize=7),
            ax.plot(rank["n"], rank["ops"], "bs-", label="Union by Rank - O(log n)", linewidth=2, markersize=7),
            ax.plot(tarjan["n"], tarjan["ops"], "g^-", label="Tarjan - O(alpha(n))", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Operacoes de acesso (escala log)"),
            ax.set_yscale("log"),
            ax.legend(),
        ),
    )

    salvar_grafico(
        "grafico_custo_por_aresta.png",
        "Custo Medio por Aresta - Evidencia de Complexidade Amortizada",
        lambda ax: (
            ax.plot(naive["n"], [ops / m for ops, m in zip(naive["ops"], naive["m"])], "ro-", label="Naive", linewidth=2, markersize=7),
            ax.plot(rank["n"], [ops / m for ops, m in zip(rank["ops"], rank["m"])], "bs-", label="Union by Rank", linewidth=2, markersize=7),
            ax.plot(tarjan["n"], [ops / m for ops, m in zip(tarjan["ops"], tarjan["m"])], "g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Operacoes medias por aresta"),
            ax.legend(),
        ),
    )

    print()
    print("Todos os graficos foram gerados com sucesso!")


if __name__ == "__main__":
    main()
