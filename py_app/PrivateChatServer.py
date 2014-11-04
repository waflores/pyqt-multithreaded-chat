#!/bin/env python

## @file  filename
#  @brief Detailed testcase description can go here.
#         Notice:
#         - All Doxygen comment blocks must start with ##
#         - Comments must preceed the code block they refer to.
#         - Package's name must exactly match filename with no extension
#           eg. filename: testcase.py -> package name: testcase
# @details
#   Here are some details!

"""PrivateChatServer.

This is the API for the PrivateChatServer for the app.

This script contains the main exception classes that are called during
testing. The aim is to reduce boilerplate within the actual test case
itself. We're following a MVC design pattern for the API where this is
the View and Controller for the testing.
"""

import os
import re
import sys
import subprocess

# should be using argparse for Python-2.7+
from optparse import OptionParser

class UnsupportedChatProtocolException(RuntimeError):
    
    """This error occurred because it did."""
    
    pass

class PrivateChatServer():
    def __init__(self):
        pass

## @brief Top level code from main
# @param argv List of command line arguments
# @return 0 sucess, non-zero error
def main(argv):
    usage_text = '\n\
    %prog --option1\n\
    %prog --option2\n\
    %prog --option3'
    
    # Check options
    opt = OptionParser(description='This testcase is a demo.', 
                                    usage=usage_text)
    opt.add_option('-n',  '--name',  action='store_true',
                             help='Description of the option')
    
    # Do something with these options
    (options,  args) = opt.parse_args()
    
    return 0

if __name__ == '__main__':
    print "HI!"
    #main(sys.argv)
