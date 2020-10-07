package com.heybuddy.Model;

public class CreatedUserDetail {

    private String uid;
    private String password;
    private String email;
    private String photo;

  /*  public User( String email, String photo, String password ){
        this.email = email;
        this.photo = photo;
        this.password = password;
    }*/

   /* public void saveToFirebase(){
        User userDetail = new CreatedUserDetail();
        userDetail.setEmail_id( email );
        userDetail.setImage( photo );

        FirebaseDatabase.getInstance()
                .getReference()
                .child( "Users" )
                .child( uid ).setValue( userDetail );
    }

    private void uploadProfileImage(){
        final StorageReference mStorageReference = FirebaseStorage.getInstance()
                .getReference()
                .child( "pics" +
                        uid +
                        photo.substring( photo.lastIndexOf( "." ) ) );

        UploadTask uploadTask = mStorageReference.putFile( Uri.fromFile( new File( photo ) ) );
        uploadTask.continueWithTask( new Continuation< UploadTask.TaskSnapshot, Task< Uri >>(){
            @Override
            public Task< Uri > then( @NonNull Task< UploadTask.TaskSnapshot > task ) throws Exception{
                if( !task.isSuccessful() )
                    throw task.getException();

                return mStorageReference.getDownloadUrl();
            }
        } ).addOnCompleteListener( new OnCompleteListener< Uri >(){
            @Override
            public void onComplete( @NonNull Task< Uri > task ){
                if( task.isSuccessful() ){
                    photo = task.getResult().toString();
                    FirebaseDatabase.getInstance().getReference( "Users" )
                            .child( uid )
                            .child( "image" )
                            .setValue( photo );
                }
            }
        } );
    }*/
}
