package br.com.marketimobi.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;

import br.com.marketimobi.api.config.property.MarketimobiApiProperty;

@Profile("s3")
@Configuration
public class S3Config {
	
	@Autowired
	private MarketimobiApiProperty property;
	
	@Bean
	public AmazonS3 amazonS3() {
		AWSCredentials credenciais = new BasicAWSCredentials(
				property.getS3().getAccessKeyId(), property.getS3().getSecretAccessKey());
		
		AmazonS3 amazonS3 = AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(credenciais))
				.withClientConfiguration(new ClientConfiguration()
						.withMaxErrorRetry(10)
						.withConnectionTimeout(120 * 1000)
						.withSocketTimeout(120 * 1000)
						.withTcpKeepAlive(true)
						.withMaxConnections(100)
						)
				.build();
		
		if (!amazonS3.doesBucketExistV2(property.getS3().getBucket())) {
			amazonS3.createBucket(
					new CreateBucketRequest(property.getS3().getBucket()));
			
			BucketLifecycleConfiguration.Rule regraExpiracao = 
					new BucketLifecycleConfiguration.Rule()
					.withId("Regra de expiração de arquivos temporários")
					.withFilter(new LifecycleFilter(
							new LifecycleTagPredicate(new Tag("expirar", "true"))))
					.withExpirationInDays(1)
					.withStatus(BucketLifecycleConfiguration.ENABLED);
			
			BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
					.withRules(regraExpiracao);
			
			amazonS3.setBucketLifecycleConfiguration(property.getS3().getBucket(), 
					configuration);
		}
		
		return amazonS3;
	}

}
