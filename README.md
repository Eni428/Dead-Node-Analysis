# Dead-Node-Analysis
Dead Node Analysis: A Retraining-Free Pruning Methodology for Three-Layer Perceptrons
# Dead Node Analysis: A Retraining-Free Pruning Methodology for Three-Layer Perceptrons

## Overview

This repository contains the source code, datasets, trained neural network weights, and experimental results used in the research paper:

**Dead Node Analysis: A Retraining-Free Pruning Methodology for Three-Layer Perceptrons**

The proposed methodology investigates the behaviour of hidden neurons in a three-layer perceptron trained for binary image classification (Circle vs Rectangle). The main objective is to identify hidden neurons that contribute little to the classification process and analyse possible network pruning without requiring additional retraining.

This repository provides the implementation necessary to understand and reproduce the proposed methodology.

For simplicity and repository size considerations, only **one representative dataset** is included in this repository. The implementation is identical to the one used for all experiments reported in the paper.

---

# Repository Structure

```
Dead-Node-Analysis/

├── src/
│      First.java
│      HiddenNeuronAnalyzer.java
│      PrunedNeuronAnalyzer.java
│
├── datasets/
│      38_images/
│          learn/
│          test/
│
├── weights/
│      WEIGHT1.txt
│      WEIGHT2.txt
│      BIGGEST.txt
│      SMALLEST.txt
│
├── results/
│      figures/
│      tables/
│
├── docs/
│
└── README.md
```

---

# Source Code Description

## First.java

This program implements the three-layer perceptron used throughout the research.

It performs the following tasks:

- Loads the image datasets.
- Executes the neural network.
- Performs feed-forward classification.
- Generates the hidden neuron activation values.
- Produces the trained network weights.
- Stores the classification outputs.

The program generates the following files:

- **DimensionHID.txt**
- **WEIGHT1.txt**
- **WEIGHT2.txt**
- **RESPONSE.txt**

---

## HiddenNeuronAnalyzer.java

This program analyses the behaviour of the hidden neurons after the network has been trained.

Its responsibilities include:

- Reading hidden neuron activations from **DimensionHID.txt**.
- Computing the mean activation of every hidden neuron.
- Computing the standard deviation.
- Calculating a separability score that measures how effectively each hidden neuron distinguishes the two classes.
- Ranking neurons according to their importance.
- Identifying neurons that are potential pruning candidates.
- Evaluating different hidden-layer sizes (1–10 neurons).
- Estimating the optimal hidden layer architecture.
- Saving the neuron analysis into **NeuronAnalysisResults.txt**.

This analysis helps determine which hidden neurons contribute most to classification performance.

---

## PrunedNeuronAnalyzer.java

This program evaluates hidden neuron importance using the trained output weights.

The program:

- Reads **WEIGHT2.txt**.
- Computes an importance score for every hidden neuron by summing the absolute values of its output weights.
- Compares each importance score against a predefined pruning threshold.
- Classifies every hidden neuron as either:

- **KEEP**
- **REMOVE**

This produces a pruning recommendation based on the contribution of each hidden neuron to the output layer.

---

# Datasets

The original study presented in the paper was experimentally validated using **ten independently generated datasets** of different sizes.

To keep this repository lightweight and easy to use, only **one representative dataset (38_images)** is included.

The same implementation, workflow, and experimental procedure were applied to all datasets used in the paper.

Dataset structure:

```
datasets/

└── 38_images/
      ├── learn/
      └── test/
```

The **learn** folder contains the training images.

The **test** folder contains the testing images used to evaluate the trained neural network.

---

# Weight Files

## WEIGHT1.txt

Contains the trained weights connecting the **Input Layer** to the **Hidden Layer**.

These weights determine how every input feature contributes to the hidden neurons.

---

## WEIGHT2.txt

Contains the trained weights connecting the **Hidden Layer** to the **Output Layer**.

These weights are analysed during the pruning stage to estimate the importance of every hidden neuron.

---

## BIGGEST.txt

Contains the maximum activation values recorded for each hidden neuron during the experiments.

These values help analyse neuron activation behaviour.

---

## SMALLEST.txt

Contains the minimum activation values recorded for each hidden neuron.

Together with **BIGGEST.txt**, these files describe the activation range of every hidden neuron.

---

# Results

The **results** directory contains the outputs generated during the experiments, including:

- Classification results
- Hidden neuron analysis
- Pruning analysis
- Experimental figures
- Experimental tables

These results correspond to the experiments presented in the associated research paper.

---

# How to Run

### Step 1

Clone or download this repository.

---

### Step 2

Open the Java project using Eclipse IDE (recommended) or another Java IDE.

---

### Step 3

Ensure that the dataset is located inside the **datasets** folder.

---

### Step 4

Run:

```
First.java
```

This generates:

- DimensionHID.txt
- WEIGHT1.txt
- WEIGHT2.txt
- RESPONSE.txt

---

### Step 5

Run:

```
HiddenNeuronAnalyzer.java
```

This analyses hidden neuron activations, ranks neurons according to their importance, and estimates the optimal hidden layer size.

---

### Step 6

Run:

```
PrunedNeuronAnalyzer.java
```

This analyses **WEIGHT2.txt** and identifies which hidden neurons should be kept or removed according to their importance.

---

# Software Requirements

- Java JDK 11 or later
- Eclipse IDE (recommended)

---

# Citation

If you use this repository in your research, please cite the corresponding publication once available.
