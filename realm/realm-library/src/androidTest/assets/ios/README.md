This folder contains various Realm databases created on iOS and can be used to test interop with
Realm-Android.

The databases are generated using the below iOS code.
Note that debugging must be disabled as it is currently (02/10-2015) not possible to debug encrypted
Realms in Xcode.

### HOWTO

1. Checkout realm-cocoa.
2. Open ~/realm-cocoa/RealmExamples.xcodeproj in Xcode.
3. Replace /Simple/AppDelegate.m with the below code.
4. Disable debugging. See below for how.
5. Run Simple project.
6. Copy/paste output Realm files into Java unit tests asset directory.

### DISABLE DEBUGGING:

1. Click on spinner that chooses which Example to run.
2. At the bottom should be a button called "Edit Scheme".
3. Choose "Run" if not selected already.
4. Remove check in "Debug executable".
5. Save and run.

See the Log for where the output files are located.

```objective-c  
////////////////////////////////////////////////////////////////////////////
//
// Copyright 2015 Realm Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

#import "AppDelegate.h"
#import <Realm/Realm.h>

@interface IOSChild : RLMObject
@property NSString      *name;
@end
RLM_ARRAY_TYPE(IOSChild)

@implementation IOSChild
@end

@interface IOSAllTypes : RLMObject
@property long id;
@property bool boolCol;
@property short shortCol;
@property int intCol;
@property long longCol;
@property float floatCol;
@property double doubleCol;
@property NSData *byteCol;
@property NSString *stringCol;
@property NSDate *dateCol;
@property IOSChild *child;
@property RLMArray<IOSChild> *children;
@end
RLM_ARRAY_TYPE(AllTypes)

@implementation IOSAllTypes
+ (NSString *)primaryKey {
    return @"id";
}
@end

@implementation AppDelegate

+ (RLMRealm *)appDefaultRealm:(NSString *) realmName {
    NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)objectAtIndex:0];
    NSString* allTypesRealm = [documentsDirectory stringByAppendingPathComponent:realmName];
    [[NSFileManager defaultManager] removeItemAtPath:allTypesRealm error:nil];
    RLMRealm *realm = [RLMRealm realmWithPath:allTypesRealm];
    return realm;
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.rootViewController = [[UIViewController alloc] init];
    [self.window makeKeyAndVisible];

    NSLog(@"Documents Directory: %@", [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject]);

    const NSString *version = @"0.98.1";
    const unsigned char no_bytes[] = {};
    const unsigned char bytes[] = {1,2,3};

    RLMRealm *realm = [AppDelegate appDefaultRealm:[NSString stringWithFormat:@"%@-alltypes.realm", version]];
    [realm beginWriteTransaction];
    for (int i = 0; i < 10; i++) {
        IOSAllTypes *obj = [[IOSAllTypes alloc] init];
        obj.id = i + 1;
        obj.boolCol = TRUE;
        obj.shortCol = 1 + i;
        obj.intCol = 10 + i;
        obj.longCol = 100 + i;
        obj.floatCol = 1.23 + i;
        obj.doubleCol = 1.234 + i;
        obj.byteCol = [NSData dataWithBytes:bytes length:sizeof(bytes)];
        obj.stringCol = [NSString stringWithFormat: @"%@ %d", @"String", i];
        obj.dateCol = [NSDate dateWithTimeIntervalSince1970: (1000 + i)];

        obj.child = [[IOSChild alloc] init];
        obj.child.name = @"Foo";
        for (int j = 0; j < 10; j++) {
            IOSChild *c = [[IOSChild alloc] init];
            c.name = [NSString stringWithFormat: @"Name: %d", i];
            [obj.children addObject:c];
        }
        [realm addObject:obj];
    }
    [realm commitWriteTransaction];

    realm = [AppDelegate appDefaultRealm:[NSString stringWithFormat:@"%@-alltypes-default.realm", version]];
    [realm beginWriteTransaction];
    IOSAllTypes *obj = [[IOSAllTypes alloc] init];
    obj.byteCol = [NSData dataWithBytes:no_bytes length:sizeof(no_bytes)];
    obj.stringCol = @"";
    obj.dateCol = [NSDate dateWithTimeIntervalSince1970: 0];
    [realm addObject:obj];
    [realm commitWriteTransaction];

    realm = [AppDelegate appDefaultRealm:[NSString stringWithFormat:@"%@-alltypes-min.realm", version]];
    [realm beginWriteTransaction];
    obj = [[IOSAllTypes alloc] init];
    obj.boolCol = FALSE;
    obj.shortCol = SHRT_MIN;
    obj.intCol = INT_MIN;
    obj.longCol = LONG_MIN;
    obj.floatCol = -FLT_MAX;
    obj.doubleCol = -DBL_MAX;
    obj.byteCol = [NSData dataWithBytes:no_bytes length:sizeof(no_bytes)];
    obj.stringCol = @"";
    obj.dateCol = [NSDate dateWithTimeIntervalSinceReferenceDate: (NSTimeInterval) -DBL_MAX];
    [realm addObject:obj];
    [realm commitWriteTransaction];

    realm = [AppDelegate appDefaultRealm:[NSString stringWithFormat:@"%@-alltypes-max.realm", version]];
    [realm beginWriteTransaction];
    obj = [[IOSAllTypes alloc] init];
    obj.boolCol = TRUE;
    obj.shortCol = SHRT_MAX;
    obj.intCol = INT_MAX;
    obj.longCol = LONG_MAX;
    obj.floatCol = FLT_MAX;
    obj.doubleCol = DBL_MAX;
    obj.byteCol = [NSData dataWithBytes:no_bytes length:sizeof(no_bytes)];
    obj.stringCol = @"";
    obj.dateCol = [NSDate dateWithTimeIntervalSinceReferenceDate: (NSTimeInterval) DBL_MAX];
    [realm addObject:obj];
    [realm commitWriteTransaction];

    uint8_t buffer[64];
    for (int i = 0; i < sizeof(buffer); i++) {
        buffer[i] = 1;
    }
    NSData *keyData = [[NSData alloc] initWithBytes:buffer length:sizeof(buffer)]; // Zerofilled byte array
    NSError *error;
    RLMRealmConfiguration *config = [RLMRealmConfiguration defaultConfiguration];
    config.path = [NSString stringWithFormat:@"%@-alltypes-default-encrypted.realm", version];
    config.encryptionKey = keyData;
    config.readOnly = NO;
    realm = [RLMRealm realmWithConfiguration:config error:&error];
    [realm beginWriteTransaction];
    obj = [[IOSAllTypes alloc] init];
    obj.byteCol = [NSData dataWithBytes:no_bytes length:sizeof(no_bytes)];
    obj.stringCol = @"";
    obj.dateCol = [NSDate dateWithTimeIntervalSince1970: 0];
    [realm addObject:obj];
    [realm commitWriteTransaction];

    NSLog(@"Done");
    return YES;
}
@end
```