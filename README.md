# Directed Acyclic Word Graph
Yet another Java library for storing (and searching) strings in a Directed Acyclic Word Graph aka Minimal Acyclic Finite-State Automaton

This is another java implementation of algorithm described in https://www.aclweb.org/anthology/W98-1305.pdf. It was written in 2015 for a Natural Language Processing engine and now it's available here. The current implementation uses binary search for both compilation and search and requires less memory for large alphabet dictionaries.

# Dependencies
No dependencies except for JUnit4, used for testing.

# Usage
``` java
// Create modifiable automaton
Automaton auto = new Automaton();

// Add strings
auto.add("some word or phrase");

// Can check if string is present in the automaton
boolean result = auto.contains("some word or phrase");

// Can list all suffixes for given prefix
List<String> suffixes = auto.listSuffixes("some word");

// ... or can list all entries
List<String> entries = auto.listSuffixes("");

// Can also save as a read-only binary file (needs less space)
auto.save(new FileOutputStream("mydict_readonly.bin"), false);

// ... or as in modifiable format
auto.save(new FileOutputStream("mydict_modifiable.bin"), true);

// Read binary file into a read-only search instance
ISearch dict = Dictionary.load("mydict_readonly.bin");

// ... can do this from both format types
ISearch dict = Dictionary.load("mydict_modifiable.bin");

// And finally can also reinitialize an appendable automaton from modifiable format
Automaton newAuto = Automaton.load("mydict_modifiable.bin");

```

# Performance
Tested (and heavily used) as morphological (POS-tagging and lemmatization) dictionary core for Russian. 5M wordforms with annotations compile in about 50 seconds into a 4-5M binary file (depending on format) on i5-2400. Suffix searching speed (needed for morphologycal annotations retrieval) on the same CPU is about 250K searches per second single-threaded, with java process consuming 40-65M of memory. Automaton class is not thread safe, Dictionary class can be accessed by several threads since it is stateless.
