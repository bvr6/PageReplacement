#!/bin/bash


echo  
echo simple
java vmsim -n 2 -a second simple.trace
echo  
echo gcc
java vmsim -n 8 -a second gcc.trace
echo  
echo gzip
java vmsim -n 8 -a second gzip.trace
echo  
echo swim
java vmsim -n 8 -a second swim.trace

echo  
echo simple
java vmsim -n 2 -a opt simple.trace
echo  
echo gcc
java vmsim -n 2 -a opt gcc.trace
echo  
echo gzip
java vmsim -n 2 -a opt gzip.trace
echo  
echo swim
java vmsim -n 2 -a opt swim.trace

echo  
echo simple
java vmsim -n 3 -a lru simple.trace
echo  
echo gcc
java vmsim -n 2 -a lru gcc.trace
echo  
echo gzip
java vmsim -n 2 -a lru gzip.trace
echo  
echo swim
java vmsim -n 2 -a lru swim.trace