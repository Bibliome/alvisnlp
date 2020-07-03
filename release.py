#!/bin/env python3.6

import re
import sys
import os
import subprocess

def message(msg):
    sys.stdout.write(f'\u001B[33;1m{msg}\u001B[39m\n')

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

def git_add_and_commit(files, msg):
    subprocess.run(f'git add {files}', shell=True, check=True)
    subprocess.run(f'git commit -m \'{msg}\'', shell=True, check=True)
    
if len(sys.argv) == 1:
    sys.stdout.write(f'Current: {verstr(current)}\n')
elif len(sys.argv) == 2:
    INCREASE = sys.argv[1].lower()
    if INCREASE not in NUMBERS:
        raise RuntimeError(f'expected one of: {", ".join(NUMBERS)}')
    message(f'Current: {verstr(current)}')
    new = increase(current, INCREASE)
    message(f'Next: {verstr(new)}')
    dev = increase(new, 'major')
    message(f'Development: {verstr(dev)}-SNAPSHOT')

    message('Building AlvisNLP')
    subprocess.run('mvn clean install', shell=True, check=True)

    message('Install AlvisNLP')
    subprocess.run('./install.sh .test/alvisnlp', shell=True, check=True)

    message('Filling CHANGES.md')
    with open('CHANGES.md', 'a') as f:
        f.write('\n\n')
        if INCREASE == 'fix': f.write('### ')
        elif INCREASE == 'minor': f.write('## ')
        else: f.write('# ')
        f.write('{verstr(new)}\n\n')
    subprocess.run('emacs CHANGES.md', shell=True, check=True)
    git_add_and_commit('CHANGES.md', f'CHANGES.md for {verstr(new)}')

    message('Generating documentation')
    with open('docs/_includes/version', 'w') as f:
        f.write(verstr(new))
    subprocess.run('./build-reference.sh ../.test/alvisnlp/bin/alvisnlp', shell=True, cwd='docs', check=True)
    git_add_and_commit('docs/reference docs/_includes/version', f'documentation for {verstr(new)}')
    
    message('Running mvn release:prepare')
    subprocess.run(f'mvn -DreleaseVersion={verstr(new)} -DdevelopmentVersion={verstr(dev)} release:prepare', shell=True, check=True)

