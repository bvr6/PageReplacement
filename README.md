# PageReplacement
## How it works
This code simulates one of three page replacement algorithms: Optimal, Second Chance, and Least Recently Used. Once it is run, the output will display the following.
* The algorithm used
* Total memory accesses
* Total page faults
* Total writes to disk

To run, compile vmsim.java and Page.java, then run `./vmsim –n <numframes> -a <opt|second|lru> <tracefile>` on the command line.

## Trace files
Trace files are provided in the code already as examples, though custom trace files may be created instead. Appropriate trace files contain on each line either the letter "l", meaning that the access was a load, or the letter "s", representing a store access. Following the letter is a memory access point in the form of eight-digit hex notation.

```
l 0xDE34DB33F
s 0xDEADBEEF
...
```
