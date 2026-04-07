# Trabalho Pratico - FPAA

Projeto em Java para comparar tres variantes de DSU no algoritmo de Kruskal.

## Como compilar

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
```

## Como executar

```bash
java -cp out Main
```

O menu mostra:

- exemplo pequeno de Kruskal
- benchmark em formato CSV
- benchmark detalhado com medias

## Como gerar os graficos

```bash
python3 gerar_graficos.py
```
