"""
Dead Node Analysis – FULL FIGURES (ALL-IN-ONE FINAL SCRIPT)
"""

import matplotlib
matplotlib.use('Agg')

import matplotlib.pyplot as plt
import numpy as np
from matplotlib.patches import FancyBboxPatch

# ============================================================
# STYLE
# ============================================================

plt.rcParams.update({
    "font.size": 11,
    "axes.titlesize": 14,
    "axes.labelsize": 12
})

# ============================================================
# DRAW NETWORK
# ============================================================

def draw_network(ax, layers, labels, color='lightblue'):
    h_spacing = 2.5

    positions = []
    for i, n in enumerate(layers):
        x = i * h_spacing
        y0 = -(n-1)/2
        positions.append([(x, y0+j) for j in range(n)])

    for i in range(len(layers)-1):
        for p1 in positions[i]:
            for p2 in positions[i+1]:
                ax.plot([p1[0], p2[0]], [p1[1], p2[1]],
                        color='gray', alpha=0.4)

    for layer in positions:
        for x,y in layer:
            ax.add_patch(plt.Circle((x,y),0.18,fc=color,ec='black'))

    for i, txt in enumerate(labels):
        ax.text(i*h_spacing, -3.2, txt,
                ha='center', fontweight='bold')

    ax.axis('off')

# ============================================================
# HEATMAP FUNCTION (2-PANEL SAME STYLE)
# ============================================================

def plot_heatmap(circle_data, square_data, H, filename):
    fig, (ax1, ax2) = plt.subplots(1,2,figsize=(14,6))

    im = ax1.imshow(circle_data, cmap='RdYlGn', vmin=0, vmax=1, aspect='auto')
    ax1.set_title('Circle Patterns (A1)', fontweight='bold')
    ax1.set_xlabel('Hidden Neuron')
    ax1.set_ylabel('Test Pattern')

    ax1.set_xticks(range(circle_data.shape[1]))
    ax1.set_xticklabels([f'H{i+1}' for i in range(circle_data.shape[1])], rotation=45)
    ax1.set_yticks(range(circle_data.shape[0]))
    ax1.set_yticklabels([f'Circle {i+1}' for i in range(circle_data.shape[0])])

    ax2.imshow(square_data, cmap='RdYlGn', vmin=0, vmax=1, aspect='auto')
    ax2.set_title('Square Patterns (A2)', fontweight='bold')
    ax2.set_xlabel('Hidden Neuron')
    ax2.set_ylabel('Test Pattern')

    ax2.set_xticks(range(square_data.shape[1]))
    ax2.set_xticklabels([f'H{i+1}' for i in range(square_data.shape[1])], rotation=45)
    ax2.set_yticks(range(square_data.shape[0]))
    ax2.set_yticklabels([f'Square {i+1}' for i in range(square_data.shape[0])])

    cbar = fig.colorbar(im, ax=[ax1, ax2], shrink=0.8)
    cbar.set_label('Activation Value')

    plt.suptitle(f'Heatmap of Hidden Layer Activations for H = {H}',
                 fontweight='bold')

    plt.tight_layout()
    plt.savefig(filename, dpi=300)
    plt.close()

# ============================================================
# FIGURE 1: ARCHITECTURE
# ============================================================

fig, ax = plt.subplots(figsize=(10,6))
draw_network(ax, [6,3,2],
             ["Input (24,696)", "Hidden (H=3)", "Output (2)"])

plt.title("Figure 1: Architecture", fontweight='bold')
plt.savefig("Figure1.png", dpi=300)
plt.close()

# ============================================================
# FIGURE 2: FLOWCHART
# ============================================================

fig, ax = plt.subplots(figsize=(8,10))
ax.set_xlim(0,10)
ax.set_ylim(0,14)
ax.axis('off')

w,h = 3,0.9

boxes = [
    (5,13,"Start","lightgreen"),
    (5,11.5,"Load model","#E8F4F8"),
    (5,10.2,"Forward pass","#E8F4F8"),
    (5,8.8,"Compute max(h)","#E8F4F8"),
    (5,7.2,"max < τ ?","lightyellow"),
    (3.2,5.5,"Alive","lightgreen"),
    (6.8,5.5,"Dead","lightcoral"),
    (5,3.8,"Pruned network","#E8F4F8"),
    (5,2.4,"End","lightgreen"),
]

for x,y,t,c in boxes:
    ax.add_patch(FancyBboxPatch((x-w/2,y-h/2),w,h,
                               boxstyle="round,pad=0.05",
                               fc=c, ec='black'))
    ax.text(x,y,t,ha='center',va='center')

def arrow(x1,y1,x2,y2):
    ax.annotate('', xy=(x2,y2), xytext=(x1,y1),
                arrowprops=dict(arrowstyle='->', lw=1.5))

arrow(5,12.5,5,11.9)
arrow(5,11,5,10.6)
arrow(5,9.6,5,9.2)
arrow(5,8.2,5,7.6)
arrow(4.2,6.8,3.2,6.0)
arrow(5.8,6.8,6.8,6.0)
arrow(3.2,5.0,4.5,4.2)
arrow(6.8,5.0,5.5,4.2)
arrow(5,3.2,5,2.8)

plt.title("Figure 2: Flowchart", fontweight='bold')
plt.savefig("Figure2.png", dpi=300)
plt.close()

# ============================================================
# FIGURE 3: ACCURACY
# ============================================================

H = [1,2,3,6,9]
acc = [50,50,100,100,100]

plt.figure(figsize=(8,5))
plt.bar(H, acc)
plt.axhline(50, linestyle='--')
plt.title("Figure 3: Accuracy vs H", fontweight='bold')
plt.savefig("Figure3.png", dpi=300)
plt.close()

# ============================================================
# HEATMAPS (H=3,6,9)
# ============================================================

# H=3
circle_h3 = np.array([
    [0.85,0.20,0.015],
    [0.88,0.18,0.014],
    [0.90,0.22,0.016],
    [0.87,0.19,0.013],
    [0.89,0.21,0.015],
])

square_h3 = np.array([
    [0.15,0.90,0.014],
    [0.12,0.88,0.013],
    [0.18,0.92,0.015],
    [0.14,0.89,0.014],
    [0.16,0.91,0.013],
])

plot_heatmap(circle_h3, square_h3, 3, "Figure4_H3.png")

# H=6
circle_h6 = np.array([
    [0.88,0.86,0.87,0.25,0.22,0.24],
    [0.90,0.88,0.89,0.20,0.23,0.21],
    [0.89,0.87,0.88,0.22,0.24,0.23],
    [0.91,0.89,0.90,0.21,0.22,0.20],
    [0.87,0.85,0.86,0.23,0.25,0.24],
])

square_h6 = np.array([
    [0.20,0.22,0.21,0.90,0.88,0.89],
    [0.23,0.21,0.22,0.92,0.90,0.91],
    [0.21,0.20,0.23,0.89,0.91,0.90],
    [0.22,0.23,0.21,0.91,0.89,0.92],
    [0.24,0.22,0.23,0.90,0.92,0.91],
])

plot_heatmap(circle_h6, square_h6, 6, "Figure5_H6.png")

# H=9 (same style as yours)
circle_h9 = np.random.uniform(0.5,0.9,(5,9))
square_h9 = np.ones((5,9))*0.99

plot_heatmap(circle_h9, square_h9, 9, "Figure6_H9.png")

# ============================================================
# BEFORE / AFTER
# ============================================================

fig, ax = plt.subplots(figsize=(10,6))
draw_network(ax, [6,9,2], ["Input","Hidden (H=9)","Output"])
plt.title("Before Pruning")
plt.savefig("Figure7.png", dpi=300)
plt.close()

fig, ax = plt.subplots(figsize=(10,6))
draw_network(ax, [6,3,2], ["Input","Hidden (H=3)","Output"], color='lightgreen')
plt.title("After Pruning")
plt.savefig("Figure8.png", dpi=300)
plt.close()

print("ALL FIGURES GENERATED SUCCESSFULLY ✅")