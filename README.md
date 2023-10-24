## CREMEr

### About
This project was created to perform the back-breaking calculations for the CREME app, which is the final requirement of the Chemical Reaction Engineering (Kinetics) course.

### Building
While you can't directly build your own extension from this repository alone, due to Rush requiring local dependencies (i.e., data it generates in your computer), the [code](src/com/pizzashi/cremer) is documented _(although not satisfactorily)_ should you want to build your own extension regarding calculations in reaction kinetics, in Kotlin.

### Scope
The extension currently covers calculations with regard to:
1. Arrhenius Equation
2. Constant volume batch reactor
    - Irreversible reactions:
        - First-order unimolecular reactions
        - Second-order bimolecular reactions
        - Third-order trimolecular reactions
        - nth-order reactions
        - Zero-order reactions
        - Parallel reactions
        - Autocatalytic reactions
        - Shifting-order reactions

More reactions were planned, especially reversible reactions and varying volume reactors, but were not implemented due to time constraints.

### Reference
This extension bases its calculation techniques from Chemical Reaction Engineering, 3rd Edition by Octave Levenspiel.
