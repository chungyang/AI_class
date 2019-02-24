import random
import heapq
import time
import matplotlib.pyplot as plt
import sys


def getPositions(N):

    knight_x = random.randint(-N, N)
    knight_y = random.randint(-N, N)

    goal_x = random.randint(-N, N)
    goal_y = random.randint(-N, N)

    return (knight_x, knight_y), (goal_x, goal_y)


def getMoves(k_position, g_position, N):

    moves = [(2, -1), (1, -2), (-1, -2), (-2, -1), (-2, 1), (-1, 2), (1, 2), (2, 1)]
    frontier = [(0, k_position, 0)]
    explored = {}
    n_expanded = 0

    while frontier:

        current_state = heapq.heappop(frontier)
        current_position = current_state[1]

        if current_position in explored:
            continue

        explored[current_position] = 1
        n_expanded += 1
        current_g = current_state[2]

        if current_position == g_position:
            return current_g, n_expanded

        for move in moves:

            new_position = (current_position[0] + move[0], current_position[1] + move[1])

            if not new_position[0] >= -N and new_position[0] <= N and new_position[1] >= -N and new_position[1] <= N:
                continue

            if new_position not in explored:

                g = current_g + 1
                h = (abs(new_position[0] - g_position[0]) + abs(new_position[1] - g_position[1])) / 3
                f = g + h

                heapq.heappush(frontier, (f, new_position, g))


def plot_data(costs, time_consume, nodes_explored):
    fig, (ax1, ax2) = plt.subplots(2)

    ax1.scatter(costs, nodes_explored, c = 'b', label = 'nodes_explored')
    ax1.legend()
    ax1.set_xlabel('Solution Length')
    ax1.set_ylabel('Nodes Expanded')

    ax2.scatter(costs, time_consume, c = 'r', label = 'time_consume')
    ax2.legend()
    ax2.set_xlabel('Solution Length')
    ax2.set_ylabel('Time Consumed (seconds)')

    plt.show()


if __name__ == '__main__':
    N = sys.argv[0]
    N = 100
    costs = []
    times = []
    n = []
    for i in range(300):

        k_posistion, g_position = getPositions(N)
        start = time.time()
        cost, n_expanded = getMoves(k_posistion, g_position, N)
        end = time.time()

        costs.append(cost)
        times.append(end - start)
        n.append(n_expanded)

    plot_data(costs, times, n)
