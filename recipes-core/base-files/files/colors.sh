#!/bin/sh

# Enable color support for ls and define handy aliases
if [ -x /usr/bin/dircolors ]; then
    eval "`dircolors -b`"
    alias ls='ls --color=auto'
    export LS_OPTIONS='--color=auto'
fi
