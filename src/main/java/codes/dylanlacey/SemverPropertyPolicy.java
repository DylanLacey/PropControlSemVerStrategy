package codes.dylanlacey;

import java.util.Optional;


import org.apache.maven.shared.release.policy.PolicyException;
import org.apache.maven.shared.release.policy.version.VersionPolicy;
import org.apache.maven.shared.release.policy.version.VersionPolicyRequest;
import org.apache.maven.shared.release.policy.version.VersionPolicyResult;
import org.apache.maven.shared.release.versions.VersionParseException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import org.semver.Version;
import org.semver.Version.Element;

@Component(
    role = VersionPolicy.class,
    hint = "SemverPropertyPolicy",
    description = "A VersionPolicy _actually_ following the SemVer rules, using properties"
)	
public final class SemverPropertyPolicy implements VersionPolicy
{

    public VersionPolicyResult getReleaseVersion( VersionPolicyRequest request )
        throws PolicyException, VersionParseException
    {
        Version version;
        try 
        {
            version = Version.parse( request.getVersion() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new VersionParseException( e.getMessage() );
        }
        
        String releaseTypeString = Optional.ofNullable(System.getProperty("RELEASE_TYPE")).orElse("PATCH");
        System.out.println("Requested " + releaseTypeString + "release from prior version(" + version +")");
        Element releaseType = Element.valueOf(releaseTypeString.toUpperCase());

        System.out.println("Turned into " + releaseType.toString());
        if(releaseType != Element.PATCH) {
            version.next(releaseType);
        }

        System.out.println("Suggested new version is " + version);

        VersionPolicyResult result = new VersionPolicyResult();
        result.setVersion( version.toReleaseVersion().toString() );
        return result;
    }

    public VersionPolicyResult getDevelopmentVersion( VersionPolicyRequest request )
        throws PolicyException, VersionParseException
    {
        Version version;
        try 
        {
            version = Version.parse( request.getVersion() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new VersionParseException( e.getMessage() );
        }
        
        version = version.next( Element.PATCH );  
        VersionPolicyResult result = new VersionPolicyResult();
        result.setVersion( version.toString() + "-SNAPSHOT" );
        return result;
    }
}