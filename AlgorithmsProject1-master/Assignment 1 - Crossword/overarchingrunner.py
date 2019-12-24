import os
import time

# This is just a little script I whipped up to avoid typing stuff up a bunch of times
# Don't feel obliged to use it at all.

tests = ["test6b.txt"]

for test in tests:
    # First run the DLB version
    # Then run the MyDictionary version
    start_time = time.time()
    cmd = "java Crossword InputData/" + test + " DLB > " + test + "_MYDICT_out.txt"
    print("Running " + cmd)
    os.system(cmd)
    print(test + " took " + str(time.time() - start_time) + " to run w/ md")

print("Should be done, thanks for playing!")
    
