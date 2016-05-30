package recommender;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import recommender.utils.MapUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Miguel on 25-05-2016.
 */
public class RecommenderSystem
{
    private Integer numberOfUsers, numberOfItems;
    private String configFilename, algorithmName;

    private String RATINGS_PATH =  "Datasets/FilmTrust/ratings.txt";
    private String TEST_PREDICTIONS = "Datasets/FilmTrust/test.txt";
    private String TRUST_PATH =  "Datasets/FilmTrust/trust.txt";
    private String TEST_PREDICTIONS_RESULTS;

    private String[][] ratingsDataset;
    private String[][] trustDataset;
    private String[][] predictions;

    public static void main(String[] args) throws Exception
    {
        RecommenderSystem lol = new RecommenderSystem(1642, 2071, "TrustSVD.conf", "TrustSVD");

        lol.changeTrust(1, 2, "1");
        lol.rateItem(1, 3, "2.5");

        System.out.println("O user 2 tem " + lol.requestItemsNotRated(2).size() + " items n avaliados");
        lol.runAlgorithm();
        System.out.println(lol.requestRecommendation(2, 20));
    }

    /////////////////////// PUBLIC METHODS ///////////////////////////////////////////////

    public RecommenderSystem(Integer numberOfUsers, Integer numberOfItems, String configFilename, String algorithmName)
    {
        this.numberOfUsers = numberOfUsers;
        this.numberOfItems = numberOfItems;
        this.configFilename = configFilename;
        this.algorithmName = algorithmName;
        this.TEST_PREDICTIONS_RESULTS = "Results/" + algorithmName + "-rating-predictions.txt";

        // Initialize lists:
        ratingsDataset = new String[numberOfUsers][numberOfItems];
        trustDataset = new String[numberOfUsers][numberOfUsers];
        predictions = new String[numberOfUsers][numberOfItems];

        importDataset();
    }

    public List<Integer> requestItemsNotRated(Integer userID)
    {
        if (userID > numberOfUsers)
        {
            System.err.println("User ID not valid!");
            return new ArrayList<>();
        }

        List<Integer> returnArray = new ArrayList<>();

        // Return ratings not done already:
        for(int j = 0; j < ratingsDataset[userID - 1].length; j++)
        {
            if (ratingsDataset[userID - 1][j].equals("-1"))
                returnArray.add(j + 1);
        }

        return returnArray;
    }

    public List<Map.Entry<Integer, Double>> requestRecommendation(Integer userID, Integer numberOfItems)
    {
        if (userID > numberOfUsers)
        {
            System.err.println("User ID not valid!");
            return new ArrayList<>();
        }

        // Put everything in an hash set:
        String[] userPreds = predictions[userID - 1];
        Map<Integer, Double> predictionsMap = new HashMap<>();

        for(int i = 0; i < userPreds.length; i++)
        {
            // If the item is predicted and not rated before by the user lets put it in the map:
            if (!(userPreds[i].equals("-1")))
                predictionsMap.put(i + 1, Double.parseDouble(userPreds[i]));
        }

        return MapUtils.getTopK(MapUtils.orderByValueDecreasing(predictionsMap), numberOfItems);
    }

    public void runAlgorithm() throws Exception
    {
        // Write predictions to test:
        writeTestPredictions();

        // Run the algorithm
        RecommenderAlgorithm algorithm = new RecommenderAlgorithm();
        algorithm.executeLib(new String[]{"-c", "config/" + configFilename});

        System.out.println("Ja corri o algoritmo");

        // Rest predictions and load new ones:
        for (String[] array : predictions)
            Arrays.fill(array, "-1");

        readTestPredictionsResults();

        System.out.println("Já li a matriz");
    }

    public void rateItem(Integer userID, Integer itemID, String rate) throws Exception
    {
        if (userID > numberOfUsers)
        {
            System.err.println("User ID not valid!");
            return;
        }

        if (itemID > numberOfItems)
        {
            System.err.println("Item ID not valid!");
            return;
        }

        ratingsDataset[userID - 1][itemID - 1] = rate;

        // Update rate file:
        saveRatings();
    }

    public void changeTrust(Integer user1ID, Integer user2ID, String trust) throws Exception
    {
        if (user1ID > numberOfUsers)
        {
            System.err.println("User1 ID not valid!");
            return;
        }

        if (user2ID > numberOfUsers)
        {
            System.err.println("User2 ID not valid!");
            return;
        }

        ratingsDataset[user1ID - 1][user2ID - 1] = trust;

        // Update trust file:
        saveTrust();
    }

    ///////////////// PRIVATE METHODS ///////////////////////////////////////////////////////

    private void readTestPredictionsResults()
    {
        try
        {
            CSVReader testPredictionsReader = new CSVReader(new FileReader(TEST_PREDICTIONS_RESULTS), ' ');

            // Read first line (header):
            testPredictionsReader.readNext();

            String[] nextLine;
            while ((nextLine = testPredictionsReader.readNext()) != null)
            {
                Integer userID = Integer.parseInt(nextLine[0]);
                Integer itemID = Integer.parseInt(nextLine[1]);
                String predictedRating = nextLine[3];
                predictions[userID - 1][itemID - 1] = predictedRating;
            }

            testPredictionsReader.close();
        } catch (IOException e)
        {
            System.err.println("Error loading test predictions file!");
            System.exit(1);
        }
    }

    private void writeTestPredictions()
    {
        try
        {
            CSVWriter writer = new CSVWriter(new FileWriter(TEST_PREDICTIONS), ' ', CSVWriter.NO_QUOTE_CHARACTER);

            for(int i = 0; i < predictions.length; i++)
                for(int j = 0; j < predictions[i].length; j++)
                {
                    if (predictions[i][j].equals("-1"))
                        writer.writeNext(new String[]{String.valueOf(i + 1), String.valueOf(j + 1)});
                }

            writer.close();
        } catch (IOException e)
        {
            System.err.println("Error writing in test predictions file!");
            System.exit(1);
        }
    }

    private void importDataset()
    {
        // Delete all previous users:
        resetMatrixes();

        // Read all the new ones:
        readRatings();
        readTrust();
    }

    private void resetMatrixes()
    {
        for (String[] array : ratingsDataset)
            Arrays.fill(array, "-1");

        for (String[] array : trustDataset)
            Arrays.fill(array, "-1");

        for (String[] array : predictions)
            Arrays.fill(array, "-1");
    }

    private void readRatings()
    {
        try
        {
            CSVReader ratingsReader = new CSVReader(new FileReader(RATINGS_PATH), ' ');

            String[] nextLine;
            while ((nextLine = ratingsReader.readNext()) != null)
            {
                Integer userID = Integer.parseInt(nextLine[0]);
                Integer itemID = Integer.parseInt(nextLine[1]);
                String rating = nextLine[2];
                ratingsDataset[userID - 1][itemID - 1] = rating;
            }

            ratingsReader.close();
        } catch (IOException e)
        {
            System.err.println("Error loading ratings dataset file!");
            System.exit(1);
        }
    }

    private void saveRatings()
    {
        try
        {
            CSVWriter writer = new CSVWriter(new FileWriter(RATINGS_PATH), ' ', CSVWriter.NO_QUOTE_CHARACTER);

            for(int i = 0; i < ratingsDataset.length; i++)
                for(int j = 0; j < ratingsDataset[i].length; j++)
                {
                    if (!ratingsDataset[i][j].equals("-1"))
                        writer.writeNext(new String[]{String.valueOf(i + 1), String.valueOf(j + 1), ratingsDataset[i][j]});
                }

            writer.close();
        } catch (IOException e)
        {
            System.err.println("Error writing in ratings dataset file!");
            System.exit(1);
        }
    }

    private void readTrust()
    {
        try
        {
            CSVReader trustReader = new CSVReader(new FileReader(TRUST_PATH), ' ');

            String[] nextLine;
            while ((nextLine = trustReader.readNext()) != null)
            {
                Integer userID = Integer.parseInt(nextLine[0]);
                Integer user2ID = Integer.parseInt(nextLine[1]);
                String trust = nextLine[2];

                trustDataset[userID - 1][user2ID - 1] = trust;
            }

            trustReader.close();
        } catch (IOException e)
        {
            System.err.println("Error loading trust dataset file!");
            System.exit(2);
        }
    }

    private void saveTrust()
    {
        try
        {
            CSVWriter writer = new CSVWriter(new FileWriter(TRUST_PATH), ' ', CSVWriter.NO_QUOTE_CHARACTER);

            for(int i = 0; i < trustDataset.length; i++)
                for(int j = 0; j < trustDataset[i].length; j++)
                {
                    if (!trustDataset[i][j].equals("-1"))
                        writer.writeNext(new String[] {String.valueOf(i + 1), String.valueOf(j + 1), trustDataset[i][j]});
                }

            writer.close();
        } catch (IOException e)
        {
            System.err.println("Error writing in ratings dataset file!");
            System.exit(1);
        }
    }
}
