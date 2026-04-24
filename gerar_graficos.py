import csv
import sys

import matplotlib.pyplot as plt


def carregar_dados(caminho):
    dados = {"Naive": [], "Union by Rank": [], "Tarjan (Rank + Compressao)": []}

    with open(caminho, newline="", encoding="utf-8") as arquivo:
        leitor = csv.DictReader(arquivo)
        for linha in leitor:
            dados[linha["variante"]].append(
                {
                    "n": int(linha["n"]),
                    "tempo_medio": float(linha["tempo_medio"]),
                    "tempo_std": float(linha["tempo_std"]),
                    "ops_media": float(linha["ops_media"]),
                }
            )

    for variante in dados:
        dados[variante].sort(key=lambda item: item["n"])

    return dados


def salvar(nome, titulo, funcao):
    fig, ax = plt.subplots()
    funcao(ax)
    ax.set_title(titulo)
    ax.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(nome, dpi=150)
    plt.close(fig)
    print(f"Salvo: {nome}")


def main():
    caminho = sys.argv[1] if len(sys.argv) > 1 else "benchmark_detalhado.csv"
    dados = carregar_dados(caminho)

    naive = dados["Naive"]
    rank = dados["Union by Rank"]
    tarjan = dados["Tarjan (Rank + Compressao)"]

    plt.rcParams["figure.figsize"] = (10, 6)
    plt.rcParams["font.size"] = 11

    salvar(
        "grafico_operacoes_todas.png",
        "Comparacao de Operacoes - DSU no Kruskal",
        lambda ax: (
            ax.plot([d["n"] for d in naive], [d["ops_media"] for d in naive], "ro-", label="Naive", linewidth=2, markersize=7),
            ax.plot([d["n"] for d in rank], [d["ops_media"] for d in rank], "bs-", label="Union by Rank", linewidth=2, markersize=7),
            ax.plot([d["n"] for d in tarjan], [d["ops_media"] for d in tarjan], "g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Operacoes de acesso"),
            ax.legend(),
            ax.ticklabel_format(style="scientific", axis="y", scilimits=(0, 0)),
        ),
    )

    salvar(
        "grafico_operacoes_rank_tarjan.png",
        "Comparacao de Operacoes - Rank vs Tarjan",
        lambda ax: (
            ax.plot([d["n"] for d in rank], [d["ops_media"] for d in rank], "bs-", label="Union by Rank", linewidth=2, markersize=7),
            ax.plot([d["n"] for d in tarjan], [d["ops_media"] for d in tarjan], "g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Operacoes de acesso"),
            ax.legend(),
        ),
    )

    salvar(
        "grafico_tempo_todas.png",
        "Comparacao de Tempo de Execucao - DSU no Kruskal",
        lambda ax: (
            ax.errorbar([d["n"] for d in naive], [d["tempo_medio"] for d in naive], yerr=[d["tempo_std"] for d in naive], fmt="ro-", label="Naive", linewidth=2, markersize=7, capsize=4),
            ax.errorbar([d["n"] for d in rank], [d["tempo_medio"] for d in rank], yerr=[d["tempo_std"] for d in rank], fmt="bs-", label="Union by Rank", linewidth=2, markersize=7, capsize=4),
            ax.errorbar([d["n"] for d in tarjan], [d["tempo_medio"] for d in tarjan], yerr=[d["tempo_std"] for d in tarjan], fmt="g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7, capsize=4),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Tempo (ms)"),
            ax.legend(),
        ),
    )

    salvar(
        "grafico_tempo_rank_tarjan.png",
        "Comparacao de Tempo - Rank vs Tarjan",
        lambda ax: (
            ax.errorbar([d["n"] for d in rank], [d["tempo_medio"] for d in rank], yerr=[d["tempo_std"] for d in rank], fmt="bs-", label="Union by Rank", linewidth=2, markersize=7, capsize=4),
            ax.errorbar([d["n"] for d in tarjan], [d["tempo_medio"] for d in tarjan], yerr=[d["tempo_std"] for d in tarjan], fmt="g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7, capsize=4),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Tempo (ms)"),
            ax.legend(),
        ),
    )

    salvar(
        "grafico_log_operacoes.png",
        "Comparacao em Escala Logaritmica - Classes de Complexidade",
        lambda ax: (
            ax.plot([d["n"] for d in naive], [d["ops_media"] for d in naive], "ro-", label="Naive - O(n)", linewidth=2, markersize=7),
            ax.plot([d["n"] for d in rank], [d["ops_media"] for d in rank], "bs-", label="Union by Rank - O(log n)", linewidth=2, markersize=7),
            ax.plot([d["n"] for d in tarjan], [d["ops_media"] for d in tarjan], "g^-", label="Tarjan - O(alpha(n))", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Operacoes de acesso (escala log)"),
            ax.set_yscale("log"),
            ax.legend(),
        ),
    )

    salvar(
        "grafico_custo_por_aresta.png",
        "Custo Medio por Aresta - Evidencia de Complexidade Amortizada",
        lambda ax: (
            ax.plot([d["n"] for d in naive], [d["ops_media"] / (3 * d["n"]) for d in naive], "ro-", label="Naive", linewidth=2, markersize=7),
            ax.plot([d["n"] for d in rank], [d["ops_media"] / (3 * d["n"]) for d in rank], "bs-", label="Union by Rank", linewidth=2, markersize=7),
            ax.plot([d["n"] for d in tarjan], [d["ops_media"] / (3 * d["n"]) for d in tarjan], "g^-", label="Tarjan (Rank + Compressao)", linewidth=2, markersize=7),
            ax.set_xlabel("Numero de vertices (n)"),
            ax.set_ylabel("Operacoes medias por aresta"),
            ax.legend(),
        ),
    )

    print()
    print("Todos os graficos foram gerados com sucesso!")


if __name__ == "__main__":
    main()
