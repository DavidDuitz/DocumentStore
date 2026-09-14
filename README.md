# Document Store and Search Engine

A Java-based document storage and search system developed as a semester-long Data Structures project at Yeshiva University.

This repository contains **Stage 6**, the completed final version of the project. The system combines several custom-built data structures to support document storage, search, undo functionality, memory management, and persistence across RAM and disk.

## Features

- Store and retrieve text and binary documents using unique URIs
- Associate documents with key-value metadata
- Search documents by keyword, prefix, and metadata
- Delete individual documents or groups of matching documents
- Undo the most recent operation or the most recent operation on a specific document
- Enforce configurable limits on document count and memory usage
- Track document usage using a least-recently-used (LRU) policy
- Automatically move documents between memory and disk when limits are exceeded
- Serialize documents to disk as JSON using GSON

## Data Structures & Algorithms

The project integrates several custom data structures implemented throughout the course:

- **Hash Table** — generic key-value storage using separate chaining
- **Stack** — command history for undo functionality
- **Trie** — keyword and prefix-based search
- **Min-Heap** — LRU document tracking and memory management
- **B-Tree** — primary document storage with support for disk persistence

The implementation also makes use of Java generics, interfaces, comparators, recursion, lambda expressions, and object-oriented design.

## Document Representation

Each document is identified by a unique `URI` and contains either:

- Plain text stored as a `String`
- Binary data stored as a `byte[]`

Documents may also contain metadata, and text documents maintain word-count information used for search and ranking.

## Search

The Document Store supports:

- Keyword search
- Prefix search
- Metadata search
- Keyword + metadata search
- Prefix + metadata search

A Trie is used to index documents efficiently, with search results ranked based on matching word or prefix frequency.

## Memory Management & Persistence

The system can limit both the number of documents and the total number of document bytes held in memory.

A Min-Heap tracks document usage time so the least-recently-used document can be identified when memory limits are exceeded.

In Stage 6, documents are not permanently deleted when removed from memory. Instead, they are moved to disk through the B-Tree and serialized as JSON using GSON. When accessed again, they are automatically restored to memory.

## Technologies

- Java 17
- Maven
- JUnit
- GSON
- Data Structures & Algorithms
- Object-Oriented Programming

## About the Project

The project was developed incrementally across six stages, with each stage introducing new data structures and functionality.

The final implementation combines searching, undo operations, memory management, and disk persistence into a single document storage system.

## Academic Project

Developed as part of the **Data Structures (COM 1320)** course at **Yeshiva University**.
