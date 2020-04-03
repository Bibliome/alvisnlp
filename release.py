#!/bin/env python

import re
import sys
import os

GIT_PROPERTIES = 'alvisnlp-core/target/classes/fr/inra/maiage/bibliome/alvisnlp/core/app/AlvisNLPGit.properties'

PROP_TAG = re.compile(r'^git\.closest\.tag\.name=(?P<major>\d+)\.(?P<minor>\d+).(?P<fix>\d+)$', re.MULTILINE)
PROP_BRANCH = re.compile(r'^git\.branch=(?P<branch>.*)$', re.MULTILINE)

NUMBERS = ('major', 'minor', 'fix')

with open(GIT_PROPERTIES) as f:
    props = f.read()
    #sys.stderr.write(props)
    m = PROP_BRANCH.search(props)
    if m is None:
        raise RuntimeError('could not find current branch')
    branch = m.group('branch').strip()
    #if branch != 'master':
    #    raise RuntimeError('current branch is not master')
    m = PROP_TAG.search(props)
    if m is None:
        raise RuntimeError('could not find MAJOR.MINOR.FIX')
    current = dict((n, int(m.group(n))) for n in NUMBERS)

def increase(version, inc):
    result = dict(version.items())
    result[inc] += 1
    if inc == 'major':
        result['minor'] = 0
        result['fix'] = 0
    elif inc == 'minor':
        result['fix'] = 0
    return result

def verstr(version):
    return '.'.join(str(version[n]) for n in NUMBERS)
        
if len(sys.argv) == 1:
    sys.stdout.write('Current: %s\n' % verstr(current))
elif len(sys.argv) == 2:
    INCREASE = sys.argv[1].lower()
    if INCREASE not in NUMBERS:
        raise RuntimeError('expected one of: %s' % ', '.join(NUMBERS))
    sys.stdout.write('Current: %s\n' % verstr(current))
    new = increase(current, INCREASE)
    sys.stdout.write('Next: %s\n' % verstr(new))
    dev = increase(new, 'major')
    sys.stdout.write('Development: %s-SNAPSHOT\n' % verstr(dev))
    os.system('mvn -DreleaseVersion=%s -DdevelopmentVersion=%s release:prepare' % (verstr(new), verstr(dev)))

