package org.deeplearning4j.examples.rl4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.deeplearning4j.rl4j.learning.ILearning;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscreteDense;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.mdp.toy.HardDeteministicToy;
import org.deeplearning4j.rl4j.mdp.toy.HardToyState;
import org.deeplearning4j.rl4j.mdp.toy.SimpleToy;
import org.deeplearning4j.rl4j.mdp.toy.SimpleToyState;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.learning.config.Adam;

/**
 * main example for toy DQN
 */
public class Toy {


    public static void main(String[] args) throws IOException {
        //simpleToy();
        //hardToy();
        toyAsyncNstep();

    }

    private static QLearning.QLConfiguration TOY_QL =
            new QLearning.QLConfiguration(
                    123,   //Random seed
                    100000,//Max step By epoch
                    80000, //Max step
                    10000, //Max size of experience replay
                    32,    //size of batches
                    100,   //target update (hard)
                    0,     //num step noop warmup
                    0.05,  //reward scaling
                    0.99,  //gamma
                    10.0,  //td-error clipping
                    0.1f,  //min epsilon
                    2000,  //num step for eps greedy anneal
                    true   //double DQN
            );


    private static AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration TOY_ASYNC_QL =
            new AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration(
                    123,        //Random seed
                    100000,     //Max step By epoch
                    80000,      //Max step
                    8,          //Number of threads
                    5,          //t_max
                    100,        //target update (hard)
                    0,          //num step noop warmup
                    0.1,        //reward scaling
                    0.99,       //gamma
                    10.0,       //td-error clipping
                    0.1f,       //min epsilon
                    2000        //num step for eps greedy anneal
            );


    private static DQNFactoryStdDense.Configuration TOY_NET =
             DQNFactoryStdDense.Configuration.builder()
        .l2(0.01).updater(new Adam(1e-2)).numLayer(3).numHiddenNodes(16).build();

    private static String dataDir(String id) throws IOException {
        Path p = Paths.get(System.getProperty("user.home"), "work", "rl", "toy", id);
        if (!Files.exists(p)) {
            Files.createDirectories(p);
        }
        return "" + p;
    }


    private static void simpleToy() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(dataDir("simple"), true);

        //define the mdp from toy (toy length)
        SimpleToy mdp = new SimpleToy(20);

        //define the training method
        Learning<SimpleToyState, Integer, DiscreteSpace, IDQN> dql = new QLearningDiscreteDense<>(mdp, TOY_NET, TOY_QL, manager);

        //enable some logging for debug purposes on toy mdp
        mdp.setFetchable(dql);

        //start the training
        dql.train();

        //useless on toy but good practice!
        mdp.close();

    }

    private static void hardToy() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(dataDir("hard"), true);

        //define the mdp from toy (toy length)
        MDP<HardToyState, Integer, DiscreteSpace> mdp = new HardDeteministicToy();

        //define the training
        ILearning<HardToyState, Integer, DiscreteSpace> dql = new QLearningDiscreteDense<>(mdp, TOY_NET, TOY_QL, manager);

        //start the training
        dql.train();

        //useless on toy but good practice!
        mdp.close();


    }


    private static void toyAsyncNstep() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(dataDir("async"), true);

        //define the mdp
        SimpleToy mdp = new SimpleToy(20);

        //define the training
        AsyncNStepQLearningDiscreteDense<SimpleToyState> dql = new AsyncNStepQLearningDiscreteDense<>(mdp, TOY_NET, TOY_ASYNC_QL, manager);

        //enable some logging for debug purposes on toy mdp
        mdp.setFetchable(dql);

        //start the training
        dql.train();

        //useless on toy but good practice!
        mdp.close();

    }

}
