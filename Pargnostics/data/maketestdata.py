#!/usr/bin/python

f = open("test.stf", "w")

f.write("# Test data\n")
f.write("2\n")
f.write("a\tReal\n")
f.write("b\tReal\n")

for i in range(1, 4):
	for j in range(3, 0, -1):
		f.write("%d\t%d\n" % (i, j))


f.close()