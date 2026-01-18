# Linked Data Playground

This project is a kickstart based on the ideas in [this document](docs/potential%20libraries.md).

## Philosophy

- **Data-First**: Schemas are optional and late-bound.
- **Graph-Native**: Uses DataScript for an in-memory knowledge graph.
- **REPL-Driven**: Most functionality is designed to be explored in the REPL.

## Structure

- `src/linked_data/core.clj`: Main entry point. Contains the DataScript DB, HTTP handlers (Ring/Reitit), and basic graph operations.
- `src/linked_data/obsidian.clj`: Parsers for Markdown and YAML frontmatter (Obsidian compatibility).
- `src/linked_data/scraper.clj`: HTML parsing utilities using Hickory.
- `src/linked_data/analysis.clj`: CSV ingestion for statistical exploration.
- `src/linked_data/rdf_export.clj`: Utilities to export the internal graph to RDF triples.

## Getting Started

1.  **Start a REPL**.
2.  Load `linked-data.core`.
3.  Run `(start-server)` to fire up the Jetty server on port 3000.
4.  Explore the `comment` blocks in each file for usage examples.

## Dependencies

Defined in `deps.edn`:
- `ring`, `reitit`: HTTP & Routing
- `datascript`: Graph DB
- `markdown-clj`: Markdown parsing
- `hickory`: HTML parsing
- `malli`: Schemas
- `clj-yaml`: YAML parsing
- `data.csv`: CSV handling
