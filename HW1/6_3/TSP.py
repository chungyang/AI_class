import random
import math
import pickle
import heapq

import matplotlib.pyplot as plt
import time

def generate_tsp(n_cities, eps):
    """
    :param n_cities: number of cities in the problem
    :param eps: minimum distance between any pair of cities
    :return: a dictionary of 2D city coordinates
    """

    cities = {}
    c_n = 0

    while len(cities) != n_cities:

        x1 = random.random()
        y1 = random.random()
        valid_city = True

        for city_n in cities:
            dist = getdistance((x1, y1), cities[city_n])

            if dist < eps:
                valid_city = False
                break

        if valid_city:
            cities[c_n] = (x1, y1)
            c_n += 1

    return cities


def getdistance(c1, c2):
    """
    :param c1: coordinate of city 1
    :param c2: coordinate of city 2

    :return: Euclidean distance between two coordinates
    """

    return math.sqrt((c2[0] - c1[0])**2 + (c2[1] - c1[1])**2)

def min_key_and_edge(distances, visited, edges):

    # Initilaize min value
    min = math.inf

    for city_n in distances:
        if distances[city_n] < min and city_n not in visited:
            min = distances[city_n]
            min_edge = edges[city_n]
            min_index = city_n

    return min_index, min_edge

def mst(cities):
    """
    :param cities: list of cities
    :return: MST cost for the cities and MST path
    """
    if not cities or len(cities) == 1:
        return 0, []


    visited = {}
    distances = {}
    edges = {}
    mst_path = []

    for i in cities:
        distances[i] = math.inf

    #Initialize
    city_n, distance = distances.popitem()
    distances[city_n] = 0
    edges[city_n] = (city_n,city_n)

    total_cost = 0

    while len(visited) != len(cities):
        unvisted_index, edge = min_key_and_edge(distances, visited, edges)
        mst_path.append(edge)
        current_city = cities[unvisted_index]
        total_cost += distances[unvisted_index]
        visited[unvisted_index] = 1

        for i in cities:

            dist = getdistance(current_city, cities[i])

            if dist < distances[i]:
                distances[i] = dist
                edges[i] = (unvisted_index, i)

    return total_cost, mst_path


def tsp(cities):

    start_city = cities[0]
    unvisited_cities = cities.copy()
    frontier = [(0, [0])]
    n_expanded = 0

    while frontier:

        current_state = heapq.heappop(frontier)[1]
        n_expanded += 1
        g = get_path_cost(current_state, cities, loop = False)

        if len(current_state) == len(cities):
            return current_state, n_expanded

        current_city = cities[current_state[-1]]

        # Delete all cities in the path from cities_copy for MST
        for c in current_state:
            del unvisited_cities[c]


        for city_n in cities:

            state_copy = current_state.copy()

            if city_n not in current_state:

                city = unvisited_cities[city_n]
                del unvisited_cities[city_n]

                dist1 = getdistance(current_city, city)
                mst_cost, mst_path = mst(unvisited_cities)

                dist2 = get_nearest_unvisited_city(city, unvisited_cities, city)
                nearest_to_start = get_nearest_unvisited_city(start_city, unvisited_cities, city)
                h = dist2 + mst_cost + nearest_to_start

                cost = g + dist1 + h

                state_copy.append(city_n)
                heapq.heappush(frontier, (cost, state_copy))

                unvisited_cities[city_n] = city

        for c in current_state:
            unvisited_cities[c] = cities[c]


def get_nearest_unvisited_city(city, cities, last_city = None):
    """
    :param city: reference city
    :param cities: all other unvisited cities
    :param last_city: the last unvisited city, used when cities is empty
    :return: nearest city to city
    """

    if not cities:
        if last_city:
            return getdistance(city, last_city)
        else:
            return 0

    min_dist = math.inf

    for city_n in cities:

        dist = getdistance(city, cities[city_n])
        if  dist < min_dist:
            min_dist = dist

    return min_dist


def store_cities(cities):
    f = open('cities.txt', 'wb')
    pickle.dump(cities, f)
    f.close()
    return

def load_cities(*args):

    if args:
        with open(args[0]) as f:
            lines = f.readlines()

            cities = {}

            for line in lines:
                s = line.split()
                city_n = int(s[0]) - 1
                x = float(s[2])
                y = float(s[1])
                cities[city_n] = (x, y)
    else:
        f = open('cities.txt', 'rb')
        cities = pickle.load(f)
        f.close()

    return cities


def get_path_cost(path, cities, loop = True):
    cost = 0
    current_path = path[0]
    for p in path:
        cost += getdistance(cities[current_path], cities[p])
        current_path = p

    if loop:
        return cost + getdistance(cities[current_path], cities[path[0]])

    return cost

def plot_path(paths, cities):
    x = []
    y = []
    for path in paths:
        x.append(cities[path][0])
        y.append(cities[path][1])

    x.append(cities[paths[0]][0])
    y.append(cities[paths[0]][1])

    plt.plot(x, y, 'ro-')
    title = 'Optimal Path Cost ' + str(get_path_cost(paths, cities))
    plt.title(title)
    plt.show()

def plot_data(n_cities, time_consume, nodes_explored):
    fig, (ax1, ax2) = plt.subplots(2)

    ax1.scatter(n_cities, time_consume, c = 'b', label = 'time_consume')
    ax1.legend()
    ax1.set_xlabel('number of cities')
    ax1.set_ylabel('time consuming (second)')

    ax2.scatter(n_cities, nodes_explored, c = 'r', label = 'nodes_explored')
    ax2.legend()
    ax2.set_xlabel('number of cities')
    ax2.set_ylabel('number of explored nodes')
    plt.show()

# if __name__ == "__main__":
#     times = []
#     n_explored = []
#     n_cities = []
#
#     random.seed(2)
#     for i in range(4, 28, 4):
#         cities = generate_tsp(i, 0.001)
#
#         print("start: ", i)
#         start = time.time()
#         path, n_expanded = tsp(cities)
#         end = time.time()
#         print("finsih: ", i)
#         times.append(end - start)
#         n_explored.append(n_expanded)
#         n_cities.append(i)
#
#     plot_data(n_cities, times, n_explored)

random.seed(1)
cities = generate_tsp(5, 0.001)
print(cities)
path, n_expanded = tsp(cities)
print(path)
print(get_path_cost(path, cities))





